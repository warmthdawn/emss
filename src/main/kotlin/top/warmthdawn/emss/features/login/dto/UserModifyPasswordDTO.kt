package top.warmthdawn.emss.features.login.dto

/**
 * @author takanashi
 * @since 2021-07-21
 */

data class UserModifyPasswordDTO(
    var password: String,
    var newPassword: String
)
