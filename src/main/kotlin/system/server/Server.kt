package system.server

import com.digitalpetri.modbus.codec.ModbusRequestDecoder
import com.digitalpetri.modbus.codec.ModbusResponseEncoder
import com.digitalpetri.modbus.codec.ModbusTcpCodec
import com.digitalpetri.modbus.codec.ModbusTcpPayload
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import modbus.ModbusRequestCodec
import system.core.RequestCodec
import system.core.RequestHandler
import java.net.InetSocketAddress

class Server<out T>(
    private val address: String = "localhost",
    private val port: Int = 502,
    private val requestHandler: RequestHandler,
    private val requestCodec: RequestCodec<T>,
    private val protocolPayloadType: Class<T>,
    private val protocolCodec: ChannelHandler

) {
    private val serverBootstrap = ServerBootstrap()
    private val eventLoopGroup = NioEventLoopGroup()

    private val channelInitializer = object : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
            ch.pipeline()
                .addLast(protocolCodec)
                .addLast(ProtocolToAppRequestCodec(requestCodec, protocolPayloadType))
                .addLast(RequestDispatchingHandler(requestHandler))
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