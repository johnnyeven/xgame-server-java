package com.xgame.server.common.protocol;

public final class EnumProtocol
{
    public static final int CONTROLLER_SCENE = 5;
    public static final int CONTROLLER_BASE = 4;
    public static final int CONTROLLER_BATTLE = 3;
    public static final int CONTROLLER_MSG = 2;
    public static final int CONTROLLER_MOVE = 1;
    public static final int CONTROLLER_INFO = 0;
    public static final int NPC_CONTROLLER_BATTLE = 13;
    public static final int NPC_CONTROLLER_MOVE = 11;
    //MOVE
    public static final int ACTION_MOVETO = 0;
    public static final int ACTION_MOVE = 1;
    //MSG
    public static final int ACTION_PUBLIC_MSG = 0;
    public static final int ACTION_PRIVATE_MSG = 1;
    //BATTLE
    public static final int ACTION_ATTACK = 0;
    public static final int ACTION_PREPARE_ATTACK = 2;
    public static final int ACTION_UNDERATTACK = 1;
    public static final int ACTION_SING = 3;
    //INFO
    public static final int ACTION_INIT_INFO = 0;
    public static final int ACTION_CHANGE_ACTION = 3;
    public static final int ACTION_LOGIN = 1;
    public static final int ACTION_LOGOUT = 2;
    public static final int ACTION_INIT_CHARACTER = 4;
    public static final int ACTION_REGISTER_CHARACTER = 5;
    public static final int ACTION_REQUEST_HOTKEY = 6;
    public static final int ACTION_BIND_SESSION = 7;
    //BASE
    public static final int ACTION_VERIFY_MAP = 0;
    //SCENE
    public static final int ACTION_SHOW_PLAYER = 0;

    public static final int ACK_CONFIRM = 1;
    public static final int ACK_ERROR = 0;
    public static final int ORDER_CONFIRM = 2;

    public static final int TYPE_INT = 0;
    public static final int TYPE_LONG = 1;
    public static final int TYPE_STRING = 2;
    public static final int TYPE_FLOAT = 3;
    public static final int TYPE_BOOL = 4;
    public static final int TYPE_DOUBLE = 5;
}
