package server.modbus

class UnsupportedFunctionException(functionCode: Int)
    : Exception("Unsupported function $functionCode")