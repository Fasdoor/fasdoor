package com.os.onestopper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.os.onestopper.exception.customException.ProductAlredyExistsException;
import com.os.onestopper.logger.OneStopLogger;
import com.os.onestopper.model.ApplicationUser;
import com.os.onestopper.model.ChildService;
import com.os.onestopper.model.ParentServices;
import com.os.onestopper.repository.ChildProductRepository;
import com.os.onestopper.repository.ParentProductRepository;
import com.os.onestopper.utility.CommonUtil;
import io.micrometer.common.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    private final ParentProductRepository parentProductRepository;
    private final ChildProductRepository childProductRepository;
    private final OneStopLogger oneStopLogger;
    private final CommonUtil commonUtil;
    @Autowired
    public ProductService(ParentProductRepository parentProductRepository, OneStopLogger oneStopLogger, ChildProductRepository childProductRepository, CommonUtil commonUtil) {
        this.parentProductRepository = parentProductRepository;
        this.oneStopLogger = oneStopLogger;
        this.childProductRepository = childProductRepository;
        this.commonUtil = commonUtil;
    }

    private final Logger logger = LoggerFactory.getLogger(OneStopLogger.class);
    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public void addProduct(String object, Map<String, Object> result) throws JsonProcessingException {
        ParentServices parentServices = objectMapper.readValue(object, ParentServices.class);
        String parentProductName = parentServices.getName();
        List<ChildService> childServices = parentServices.getChildServices();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = ((ApplicationUser) authentication.getPrincipal()).getEmailId();
        if (StringUtils.isBlank(parentProductName) || childServices.stream().anyMatch(child -> StringUtils.isBlank(child.getName()))) {
            result.put("error", "All Field Should Be Filled");
        } else {
            parentProductRepository.findByName(parentProductName).ifPresent(product -> {
                throw new ProductAlredyExistsException(parentProductName.concat(" Alredy Exists"));
            });

            childServices.forEach(child -> parentProductRepository.findByName(child.getName()).ifPresent(product -> {
                throw new ProductAlredyExistsException(child.getName().concat(" Alredy Exists"));
            }));

            childServices.forEach(child -> {
                child.setCreatedOn(commonUtil.getCurrentTime());
                child.setCreatedBy(userName);
            });

            parentServices.setChildServices(childServices);
            parentServices.setCreatedBy(userName);
            parentServices.setCreatedOn(commonUtil.getCurrentTime());
            parentProductRepository.save(parentServices);
            result.put("success", "Product Added Successfully");
        }
    }
}
