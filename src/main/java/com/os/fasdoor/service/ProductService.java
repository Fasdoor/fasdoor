package com.os.fasdoor.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.os.fasdoor.exception.customException.ProductAlredyExistsException;
import com.os.fasdoor.logger.OneStopLogger;
import com.os.fasdoor.model.ApplicationUser;
import com.os.fasdoor.model.ChildService;
import com.os.fasdoor.model.ParentServices;
import com.os.fasdoor.repository.ChildProductRepository;
import com.os.fasdoor.repository.ParentProductRepository;
import com.os.fasdoor.utility.CommonUtil;
import io.micrometer.common.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


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
                throw new ProductAlredyExistsException(parentProductName.concat(" Already Exists"));
            });

            childServices.forEach(child -> parentProductRepository.findByName(child.getName()).ifPresent(product -> {
                throw new ProductAlredyExistsException(child.getName().concat(" Already Exists"));
            }));

            AtomicReference<Long> childPrimaryKey =new AtomicReference<>(childProductRepository.findMaxId());
            childPrimaryKey.compareAndSet(null, 0L);
            childServices.forEach(child -> {
                childPrimaryKey.updateAndGet(v -> v + 1);
                child.setId(childPrimaryKey.get());
                child.setCreatedOn(commonUtil.getCurrentTime());
                child.setCreatedBy(userName);
            });

            Long parentPrimaryKey = parentProductRepository.findMaxId();
            if (Objects.isNull(parentPrimaryKey)) parentPrimaryKey = 0L;

            parentServices.setChildServices(childServices);
            parentServices.setCreatedBy(userName);
            parentServices.setCreatedOn(commonUtil.getCurrentTime());
            parentServices.setId(parentPrimaryKey + 1);
            parentProductRepository.save(parentServices);
            oneStopLogger.info("Product Added Successfully");
            result.put("success", "Product Added Successfully");
        }
    }

    public void deleteProduct(Map<String, Object> result, String object) throws JSONException {
        JSONObject jsonObject = new JSONObject(object);
        Gson gson = new Gson();
        Type typeReference = new TypeToken<List<String>> () {}.getType();
        JSONArray parentIdArray = jsonObject.has("parentIds") ? jsonObject.getJSONArray("parentIds") : null;
        List<String> parentIdList = Objects.isNull(parentIdArray) ? null : gson.fromJson(String.valueOf(parentIdArray), typeReference);

        JSONArray childIdArray = jsonObject.has("childIds") ? jsonObject.getJSONArray("childIds") : null;
        List<String> childIdList = Objects.isNull(childIdArray) ? null : gson.fromJson(String.valueOf(childIdArray), typeReference);
        String parentId = jsonObject.has("parentId") ? jsonObject.getString("parentId") : null;

        if (Objects.isNull(parentIdList) && Objects.nonNull(childIdList)) {
            childIdList.forEach(childId -> childProductRepository.deleteById(Long.valueOf(childId)));
            if (childProductRepository.existsByParentServiceId(Long.valueOf(parentId)).isEmpty()) parentProductRepository.deleteById(Long.valueOf(parentId));
            result.put("success", "Product Deleted Successfully");
        } else if (Objects.nonNull(parentIdList)){
            parentIdList.forEach(parent -> parentProductRepository.deleteById(Long.valueOf(parent)));
            result.put("success", "Services Delete Successfully");
        }
    }
}
