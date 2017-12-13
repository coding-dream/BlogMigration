package com.less.migration;

public final class RegexConstants {

    /**
     * �����ֻ��ţ��򵥣�
     */
    public static final String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";
    /**
     * �����ֻ��ţ���ȷ��
     * <p>�ƶ���134(0-8)��135��136��137��138��139��147��150��151��152��157��158��159��178��182��183��184��187��188</p>
     * <p>��ͨ��130��131��132��145��155��156��175��176��185��186</p>
     * <p>���ţ�133��153��173��177��180��181��189</p>
     * <p>ȫ���ǣ�1349</p>
     * <p>������Ӫ�̣�170</p>
     */
    public static final String REGEX_MOBILE_EXACT  = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$";
    /**
     * ���򣺵绰����
     */
    public static final String REGEX_TEL           = "^0\\d{2,3}[- ]?\\d{7,8}";
    /**
     * �������֤����15λ
     */
    public static final String REGEX_ID_CARD15     = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
    /**
     * �������֤����18λ
     */
    public static final String REGEX_ID_CARD18     = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
    /**
     * ��������
     */
    public static final String REGEX_EMAIL         = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    /**
     * ����URL
     */
    public static final String REGEX_URL           = "[a-zA-z]+://[^\\s]*";
    /**
     * ���򣺺���
     */
    public static final String REGEX_ZH            = "^[\\u4e00-\\u9fa5]+$";
    /**
     * �����û�����ȡֵ��ΧΪa-z,A-Z,0-9,"_",���֣�������"_"��β,�û���������6-20λ
     */
    public static final String REGEX_USERNAME      = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$";
    /**
     * ����yyyy-MM-dd��ʽ������У�飬�ѿ���ƽ����
     */
    public static final String REGEX_DATE          = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
    /**
     * ����IP��ַ
     */
    public static final String REGEX_IP            = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

    ///////////////////////////////////////////////////////////////////////////
    // ����ժ��http://tool.oschina.net/regex
    ///////////////////////////////////////////////////////////////////////////

    /**
     * ����˫�ֽ��ַ�(������������)
     */
    public static final String REGEX_DOUBLE_BYTE_CHAR     = "[^\\x00-\\xff]";
    /**
     * ���򣺿հ���
     */
    public static final String REGEX_BLANK_LINE           = "\\n\\s*\\r";
    /**
     * ����QQ��
     */
    public static final String REGEX_TENCENT_NUM          = "[1-9][0-9]{4,}";
    /**
     * �����й���������
     */
    public static final String REGEX_ZIP_CODE             = "[1-9]\\d{5}(?!\\d)";
    /**
     * ����������
     */
    public static final String REGEX_POSITIVE_INTEGER     = "^[1-9]\\d*$";
    /**
     * ���򣺸�����
     */
    public static final String REGEX_NEGATIVE_INTEGER     = "^-[1-9]\\d*$";
    /**
     * ��������
     */
    public static final String REGEX_INTEGER              = "^-?[1-9]\\d*$";
    /**
     * ���򣺷Ǹ�����(������ + 0)
     */
    public static final String REGEX_NOT_NEGATIVE_INTEGER = "^[1-9]\\d*|0$";
    /**
     * ���򣺷��������������� + 0��
     */
    public static final String REGEX_NOT_POSITIVE_INTEGER = "^-[1-9]\\d*|0$";
    /**
     * ������������
     */
    public static final String REGEX_POSITIVE_FLOAT       = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
    /**
     * ���򣺸�������
     */
    public static final String REGEX_NEGATIVE_FLOAT       = "^-[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$";

    ///////////////////////////////////////////////////////////////////////////
    // If u want more please visit http://toutiao.com/i6231678548520731137
    ///////////////////////////////////////////////////////////////////////////
}