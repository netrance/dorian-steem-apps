package lee.dorian.steem_data.model

data class GetDynamicGlobalPropertiesParamsDTO(
    val jsonrpc: String = "2.0",
    val method: String = "condenser_api.get_dynamic_global_properties",
    val params: Array<Any> = arrayOf(),
    val id: Int
)
