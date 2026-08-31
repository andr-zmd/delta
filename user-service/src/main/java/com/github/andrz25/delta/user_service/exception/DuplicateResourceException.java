package com.github.andrz25.delta.user_service.exception;

public class DuplicateResourceException extends RuntimeException {
    private final String resourceType;
    private final String fieldName;
    private final String fieldValue;

    public DuplicateResourceException(String resourceType, String fieldName, String fieldValue) {
        this.resourceType = resourceType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;

        super(String.format("%s with %s '%s' already exists", resourceType, fieldName, fieldValue));
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }
}
