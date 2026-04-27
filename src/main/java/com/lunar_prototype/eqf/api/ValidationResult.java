package com.lunar_prototype.eqf.api;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();

    public void addError(String message) {
        errors.add(message);
    }

    public void addWarning(String message) {
        warnings.add(message);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void merge(ValidationResult other) {
        this.errors.addAll(other.errors);
        this.warnings.addAll(other.warnings);
    }

    public void merge(String prefix, ValidationResult other) {
        for (String error : other.errors) {
            this.errors.add(prefix + error);
        }
        for (String warning : other.warnings) {
            this.warnings.add(prefix + warning);
        }
    }
}
