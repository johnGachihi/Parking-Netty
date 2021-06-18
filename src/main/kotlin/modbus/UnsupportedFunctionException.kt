package modbus

class UnsupportedFunctionException(functionCode: Int)
    : Exception("Unsupported function $functionCode")