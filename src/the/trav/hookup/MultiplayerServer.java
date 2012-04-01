package the.trav.hookup;

import java.io.IOException;
import java.net.InetAddress;

import org.anddev.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.anddev.andengine.extension.multiplayer.protocol.client.connector.SocketConnectionServerConnector.ISocketConnectionServerConnectorListener;
import org.anddev.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.anddev.andengine.extension.multiplayer.protocol.server.SocketServer.ISocketServerListener;
import org.anddev.andengine.extension.multiplayer.protocol.server.SocketServerDiscoveryServer;
import org.anddev.andengine.extension.multiplayer.protocol.server.SocketServerDiscoveryServer.ISocketServerDiscoveryServerListener;
import org.anddev.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.anddev.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.anddev.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector.ISocketConnectionClientConnectorListener;
import org.anddev.andengine.extension.multiplayer.protocol.shared.IDiscoveryData.DefaultDiscoveryData;
import org.anddev.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.anddev.andengine.extension.multiplayer.protocol.util.WifiUtils;
import org.anddev.andengine.util.Debug;

public class MultiplayerServer {
	private static final int SERVER_PORT = 9321;
	private static final int DISCOVERY_PORT = 9322;
	private SocketServer<SocketConnectionClientConnector> socketServer;
	private SocketServerDiscoveryServer<DefaultDiscoveryData> socketServerDiscoveryServer;
	
	HookUpActivity activity = null;
	
	public void close() {
		socketServer.terminate();
	}
	
	public void startHosting(HookUpActivity activity) {
		this.activity = activity;
		socketServer = new SocketServer<SocketConnectionClientConnector>(SERVER_PORT, new ExampleClientConnectorListener(), new ExampleServerStateListener()) {
			@Override
			protected SocketConnectionClientConnector newClientConnector(final SocketConnection pSocketConnection) throws IOException {
				return new SocketConnectionClientConnector(pSocketConnection);
			}
		};

		socketServer.start();

//		try {
//			final byte[] wifiIPv4Address = WifiUtils.getWifiIPv4AddressRaw(activity);
//			socketServerDiscoveryServer = new SocketServerDiscoveryServer<DefaultDiscoveryData>(DISCOVERY_PORT, new ExampleSocketServerDiscoveryServerListener()) {
//				@Override
//				protected DefaultDiscoveryData onCreateDiscoveryResponse() {
//					return new DefaultDiscoveryData(wifiIPv4Address, SERVER_PORT);
//				}
//			};
//			socketServerDiscoveryServer.start();
//		} catch (final Throwable t) {
//			Debug.e(t);
//		}
	}
	
	private class ExampleClientConnectorListener implements ISocketConnectionClientConnectorListener {
		@Override
		public void onStarted(final ClientConnector<SocketConnection> pConnector) {
			activity.toast("Server: Client connected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
		}

		@Override
		public void onTerminated(final ClientConnector<SocketConnection> pConnector) {
			activity.toast("Server: Client disconnected: " + pConnector.getConnection().getSocket().getInetAddress().getHostAddress());
		}
	}

	public class ExampleSocketServerDiscoveryServerListener implements ISocketServerDiscoveryServerListener<DefaultDiscoveryData> {
		@Override
		public void onStarted(final SocketServerDiscoveryServer<DefaultDiscoveryData> pSocketServerDiscoveryServer) {
			activity.toast("DiscoveryServer: Started.");
		}

		@Override
		public void onTerminated(final SocketServerDiscoveryServer<DefaultDiscoveryData> pSocketServerDiscoveryServer) {
			activity.toast("DiscoveryServer: Terminated.");
		}

		@Override
		public void onException(final SocketServerDiscoveryServer<DefaultDiscoveryData> pSocketServerDiscoveryServer, final Throwable pThrowable) {
			Debug.e(pThrowable);
			activity.toast("DiscoveryServer: Exception: " + pThrowable);
		}

		@Override
		public void onDiscovered(final SocketServerDiscoveryServer<DefaultDiscoveryData> pSocketServerDiscoveryServer, final InetAddress pInetAddress, final int pPort) {
			activity.toast("DiscoveryServer: Discovered by: " + pInetAddress.getHostAddress() + ":" + pPort);
		}
	}
	

	private class ExampleServerConnectorListener implements ISocketConnectionServerConnectorListener {
		@Override
		public void onStarted(final ServerConnector<SocketConnection> pConnector) {
			activity.toast("Client: Connected to server.");
		}

		@Override
		public void onTerminated(final ServerConnector<SocketConnection> pConnector) {
			activity.toast("Client: Disconnected from Server...");
			activity.finish();
		}
	}

	private class ExampleServerStateListener implements ISocketServerListener<SocketConnectionClientConnector> {
		@Override
		public void onStarted(final SocketServer<SocketConnectionClientConnector> pSocketServer) {
			activity.toast("Server: Started.");
		}

		@Override
		public void onTerminated(final SocketServer<SocketConnectionClientConnector> pSocketServer) {
			activity.toast("Server: Terminated.");
		}

		@Override
		public void onException(final SocketServer<SocketConnectionClientConnector> pSocketServer, final Throwable pThrowable) {
			Debug.e(pThrowable);
			activity.toast("Server: Exception: " + pThrowable);
		}
	}
}
