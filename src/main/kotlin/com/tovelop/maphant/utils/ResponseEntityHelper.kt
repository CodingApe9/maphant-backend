package com.tovelop.maphant.utils

import org.springframework.http.ResponseEntity


fun ResponseEntity<out Any>.isSuccess() = this.statusCode == org.springframework.http.HttpStatus.OK