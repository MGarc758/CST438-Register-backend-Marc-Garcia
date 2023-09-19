package com.cst438.domain;

import java.util.Optional;

public record StudentDTO(int id, String name, String email, Optional<String> status, int status_code) {

}
