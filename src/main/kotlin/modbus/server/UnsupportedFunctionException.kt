package modbus.server

class UnsupportedFunctionException(functionCode: Int)
    : Exception("Unsupported function $functionCode")