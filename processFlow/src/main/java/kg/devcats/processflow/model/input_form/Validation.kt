package kg.devcats.processflow.model.input_form

import java.io.Serializable

data class Validation(val type: ValidationType?, val value: String?): Serializable

enum class ValidationType {
    REQUIRED, REGEX
}