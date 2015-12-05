package com.vegadvisor.client.util;

/**
 * Clase de mensaje de chat
 * Created by JuanCamilo on 05/12/2015.
 */
public class ChatMessage {

    /**
     * Usuario Session
     */
    private String userSession;

    /**
     * Usuario Other
     */
    private String userOther;

    /**
     * Fecha mensaje
     */
    private String messageDate;

    /**
     * Hora del mensaje
     */
    private String messageTime;

    /**
     * Contenido del mensaje
     */
    private String messageContent;

    /**
     * Mensaje recibido
     */
    private String messageReceived;

    /**
     * Constructor de chat Message
     *
     * @param userSession    Usuario en sesion
     * @param userOther      Otro usuario del sistema
     * @param messageDate    Fecha del mensaje
     * @param messageTime    Hora del mensaje
     * @param messageContent Contenido del mensaje
     */
    public ChatMessage(String userSession, String userOther, String messageDate,
                       String messageTime, String messageContent, String messageReceived) {
        this.userSession = userSession;
        this.userOther = userOther;
        this.messageDate = messageDate;
        this.messageTime = messageTime;
        this.messageContent = messageContent;
        this.messageReceived = messageReceived;
    }

    /****************************************
     * GETTERS Y SETTERS DEL OBJETO
     ***************************************/

    public String getUserSession() {
        return userSession;
    }

    public void setUserSession(String userSession) {
        this.userSession = userSession;
    }

    public String getUserOther() {
        return userOther;
    }

    public void setUserOther(String userOther) {
        this.userOther = userOther;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageReceived() {
        return messageReceived;
    }

    public void setMessageReceived(String messageReceived) {
        this.messageReceived = messageReceived;
    }
}
