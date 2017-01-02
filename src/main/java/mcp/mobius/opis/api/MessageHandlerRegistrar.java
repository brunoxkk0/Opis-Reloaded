package mcp.mobius.opis.api;

import java.util.HashMap;
import java.util.HashSet;

import mcp.mobius.opis.ModOpis;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;

public enum MessageHandlerRegistrar {
    INSTANCE;

    private final HashMap<Message, HashSet<IMessageHandler>> msgHandlers = new HashMap<>();

    public void registerHandler(Message msg, IMessageHandler handler) {
        if (!msgHandlers.containsKey(msg)) {
            msgHandlers.put(msg, new HashSet<>());
        }

        msgHandlers.get(msg).add(handler);
    }

    public void routeMessage(Message msg, PacketBase rawdata) {

        if (msgHandlers.containsKey(msg)) {
            msgHandlers.get(msg).stream().filter((handler) -> (!handler.handleMessage(msg, rawdata))).forEachOrdered((handler) -> {
                ModOpis.log.warn(String.format("Unhandled msg %s in handler %s", msg, handler));
            });
        } else {
            ModOpis.log.warn(String.format("Unhandled msg : %s", msg));
        }
    }
}
