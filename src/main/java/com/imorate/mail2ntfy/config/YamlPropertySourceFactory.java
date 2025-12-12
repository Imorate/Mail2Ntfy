package com.imorate.mail2ntfy.config;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    @NullMarked
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource encodedResource) throws IOException {
        Objects.requireNonNull(encodedResource, "EncodedResource must not be null");
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        Resource resource = encodedResource.getResource();
        factory.setResources(resource);
        String resourceFilename = resource.getFilename();
        try {
            factory.afterPropertiesSet();
        } catch (IllegalStateException e) {
            if (e.getCause() instanceof FileNotFoundException fnf) {
                throw new IOException("YAML resource not found: " + resourceFilename, fnf);
            }
            throw e;
        }
        Properties properties = factory.getObject();
        if (properties == null || properties.isEmpty()) {
            throw new IOException("No properties loaded from YAML resource: " + resourceFilename);
        }
        String sourceName = (name != null) ? name : resourceFilename;
        return new PropertiesPropertySource(Objects.requireNonNull(sourceName, "Property source name cannot be null"), properties);
    }

}
