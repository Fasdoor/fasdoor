package com.os.onestopper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.os.onestopper.exception.customException.ProductAlredyExistsException;
import com.os.onestopper.logger.OneStopLogger;
import com.os.onestopper.model.ParentServices;
import com.os.onestopper.repository.ChildProductRepository;
import com.os.onestopper.repository.ParentProductRepository;
import io.micrometer.common.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductService {
    private final ParentProductRepository parentProductRepository;
    private final ChildProductRepository childProductRepository;
    private final OneStopLogger oneStopLogger;
    @Autowired
    public ProductService(ParentProductRepository parentProductRepository, OneStopLogger oneStopLogger, ChildProductRepository childProductRepository) {
        this.parentProductRepository = parentProductRepository;
        this.oneStopLogger = oneStopLogger;
        this.childProductRepository = childProductRepository;
    }

    private final Logger logger = LoggerFactory.getLogger(OneStopLogger.class);
    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public void addProduct(String object, Map<String, Object> result) throws JSONException, JsonProcessingException {
        JSONObject jsonObject = new JSONObject(object);
        String parentProductName = jsonObject.has("name") ? jsonObject.getString("name") : "";
        String childProductName = jsonObject.has("childProductName") ? jsonObject.getString("childProductName") : "";
        if (StringUtils.isBlank(parentProductName) || StringUtils.isBlank(childProductName)) {
            result.put("error", "All Field Should Be Filled");
        } else {
            parentProductRepository.findByName(parentProductName).ifPresent(product -> {
                throw new ProductAlredyExistsException(parentProductName.concat(" Alredy Exists"));
            });

            parentProductRepository.findByName(childProductName).ifPresent(product -> {
                throw new ProductAlredyExistsException(childProductName.concat(" Alredy Exists"));
            });

            ParentServices parentServices = objectMapper.readValue(object, ParentServices.class);
            parentProductRepository.save(parentServices);
            result.put("success", "Product Added Successfully");
        }
    }
}
