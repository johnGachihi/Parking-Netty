package server

import com.digitalpetri.modbus.codec.ModbusRequestDecoder
import com.digitalpetri.modbus.codec.ModbusResponseEncoder
import com.digitalpetri.modbus.codec.ModbusTcpCodec
import core.RequestAction
import core.decode.RfidDecoder
import core.decode.WriteDataDecoderRegistry
import core.decode.WriteRequestDecoder
import core.decode.WriteRequestDecoderImpl
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.InetSocketAddress

class Server(
    private val address: String = "localhost",
    private val port: Int = 502,
) {
    private val serverBootstrap = ServerBootstrap()
    private val eventLoopGroup = NioEventLoopGroup()

    // Non-netty
    private val writeRequestDecoder: WriteRequestDecoder
    init {
        val registry = WriteDataDecoderRegistry()
        registry.register(RequestAction.Entry, RfidDecoder())
        writeRequestDecoder = WriteRequestDecoderImpl(registry)
    }

    private val channelInitializer = object : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
            ch.pipeline()
                .addLast(ModbusTcpCodec(ModbusResponseEncoder(), ModbusRequestDecoder()))
                .addLast("modbusTcpPayloadHandler", ModbusTcpPayloadHandler(writeRequestDecoder))
            // TODO: Add exception-handler Handler
        }
    }

    fun start() {
        try {
            bootstrapServer()
            val f = serverBootstrap.bind().sync()
            f.channel().closeFuture().sync()
        } finally {
            eventLoopGroup.shutdownGracefully().sync()
        }
    }

    private fun bootstrapServer() {
        serverBootstrap.group(eventLoopGroup)
            .channel(NioServerSocketChannel::class.java)
            .localAddress(InetSocketAddress(address, port))
            .childHandler(channelInitializer)
    }
}