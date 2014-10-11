package me.vaqxine.NetworkManager;

public class NetworkAPI {

      private NetworkManager plugin;

      public NetworkAPI(NetworkManager NM) {
            plugin = NM;
      }

      public int getNetworkPort() {
            return plugin.network_port;
      }
}
