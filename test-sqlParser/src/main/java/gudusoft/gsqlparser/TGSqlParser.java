package gudusoft.gsqlparser;

import gudusoft.gsqlparser.compiler.TGlobalScope;
import gudusoft.gsqlparser.compiler.TStackFrame;
import gudusoft.gsqlparser.nodes.TConstant;
import gudusoft.gsqlparser.nodes.TDatatypeAttribute;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTypeName;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.stmt.TCommonStoredProcedureSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUnknownSqlStatement;
import gudusoft.gsqlparser.stmt.dax.TDaxEvaluateStmt;
import gudusoft.gsqlparser.stmt.dax.TDaxExprStmt;
import gudusoft.gsqlparser.stmt.greenplum.TSlashCommand;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateProcedure;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;
import gudusoft.gsqlparser.stmt.mysql.TMySQLSource;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreatePackage;
import gudusoft.gsqlparser.stmt.oracle.TSqlplusCmdStatement;
import gudusoft.gsqlparser.stmt.teradata.TTeradataBTEQCmd;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

/**
 * 修改去掉了 输入sql长度为 10000的限制.
 */
public class TGSqlParser {
    public static EDbVendor currentDBVendor;
    private Stack<TStackFrame> frameStack = null;
    FileInputStream streamFromSqlFile = null;
    InputStreamReader sqlStreamReader = null;
    static final int stored_procedure_nested_level = 1024;
    public String sqltext;
    public String sqlfilename;
    private BufferedInputStream sqlInputStream;
    public TSourceTokenList sourcetokenlist;
    public TStatementList sqlstatements;
    private ArrayList<TSyntaxError> syntaxErrors;
    private boolean enablePartialParsing = false;
    private static String userName;
    private static String machineId;
    private static String licenseKey;
    private static String licenseType;
    private static boolean licenseOK;
    private static String licenseMessage;
    private EDbVendor dbVendor;
    private String errormessage;
    private TCustomLexer flexer;
    TCustomParser fparser;
    TCustomParser fplsqlparser;
    BufferedReader finputstream = null;
    TCustomSqlStatement gcurrentsqlstatement;
    TCustomSqlStatement nextStmt;
    TSqlCmds sqlcmds;
    HashMap sqlpluskeywordList;
    char delimiterchar;
    String defaultDelimiterStr;
    private ITokenHandle tokenHandle = null;
    private IMetaDatabase metaDatabase = null;
    char curdelimiterchar;
    String userDelimiterStr = "";
    private TSQLEnv sqlEnv = null;
    private boolean onlyNeedRawParseTree = false;

    public static EDbVendor getDBVendorByName(String dbVendorName) {
        EDbVendor vendor;
        if (dbVendorName.contains("access")) {
            vendor = EDbVendor.dbvaccess;
        } else if (dbVendorName.contains("ansi")) {
            vendor = EDbVendor.dbvansi;
        } else if (dbVendorName.contains("bigquery")) {
            vendor = EDbVendor.dbvbigquery;
        } else if (dbVendorName.contains("couchbase")) {
            vendor = EDbVendor.dbvcouchbase;
        } else if (dbVendorName.contains("dax")) {
            vendor = EDbVendor.dbvdax;
        } else if (dbVendorName.contains("db2")) {
            vendor = EDbVendor.dbvdb2;
        } else if (dbVendorName.contains("firebird")) {
            vendor = EDbVendor.dbvfirebird;
        } else if (dbVendorName.contains("generic")) {
            vendor = EDbVendor.dbvgeneric;
        } else if (dbVendorName.contains("greenplum")) {
            vendor = EDbVendor.dbvgreenplum;
        } else if (dbVendorName.contains("hana")) {
            vendor = EDbVendor.dbvhana;
        } else if (dbVendorName.contains("hive")) {
            vendor = EDbVendor.dbvhive;
        } else if (dbVendorName.contains("impala")) {
            vendor = EDbVendor.dbvimpala;
        } else if (dbVendorName.contains("informix")) {
            vendor = EDbVendor.dbvinformix;
        } else if (dbVendorName.contains("mdx")) {
            vendor = EDbVendor.dbvmdx;
        } else if (!dbVendorName.contains("mssql") && !dbVendorName.contains("sqlserver")) {
            if (dbVendorName.contains("azuresql")) {
                vendor = EDbVendor.dbvazuresql;
            } else if (dbVendorName.contains("mysql")) {
                vendor = EDbVendor.dbvmysql;
            } else if (dbVendorName.contains("netezza")) {
                vendor = EDbVendor.dbvnetezza;
            } else if (dbVendorName.contains("odbc")) {
                vendor = EDbVendor.dbvodbc;
            } else if (dbVendorName.contains("openedge")) {
                vendor = EDbVendor.dbvopenedge;
            } else if (dbVendorName.contains("oracle")) {
                vendor = EDbVendor.dbvoracle;
            } else if (!dbVendorName.contains("postgresql") && !dbVendorName.contains("postgres")) {
                if (dbVendorName.contains("redshift")) {
                    vendor = EDbVendor.dbvredshift;
                } else if (dbVendorName.contains("snowflake")) {
                    vendor = EDbVendor.dbvsnowflake;
                } else if (dbVendorName.contains("sybase")) {
                    vendor = EDbVendor.dbvsybase;
                } else if (dbVendorName.contains("teradata")) {
                    vendor = EDbVendor.dbvteradata;
                } else if (dbVendorName.contains("soql")) {
                    vendor = EDbVendor.dbvsoql;
                } else if (dbVendorName.contains("sparksql")) {
                    vendor = EDbVendor.dbvsparksql;
                } else if (dbVendorName.contains("exasol")) {
                    vendor = EDbVendor.dbvexasol;
                } else if (dbVendorName.contains("athena")) {
                    vendor = EDbVendor.dbvathena;
                } else if (dbVendorName.contains("presto")) {
                    vendor = EDbVendor.dbvpresto;
                } else if (dbVendorName.contains("trino")) {
                    vendor = EDbVendor.dbvtrino;
                } else if (dbVendorName.contains("vertica")) {
                    vendor = EDbVendor.dbvvertica;
                } else {
                    vendor = EDbVendor.dbvoracle;
                }
            } else {
                vendor = EDbVendor.dbvpostgresql;
            }
        } else {
            vendor = EDbVendor.dbvmssql;
        }

        return vendor;
    }

    public Stack<TStackFrame> getFrameStack() {
        if (this.frameStack == null) {
            this.frameStack = new Stack();
        }

        return this.frameStack;
    }

    public void setFrameStack(Stack<TStackFrame> frameStack) {
        this.frameStack = frameStack;
    }

    void closeFileStream() {
        if (this.streamFromSqlFile != null) {
            try {
                this.streamFromSqlFile.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    public TSourceTokenList getSourcetokenlist() {
        return this.sourcetokenlist;
    }

    public TStatementList getSqlstatements() {
        return this.sqlstatements;
    }

    public void setSqltext(String sqltext) {
        this.sqltext = sqltext;
        this.sqlfilename = "";
        this.sqlInputStream = null;
    }

    public String getSqltext() {
        return this.sqltext;
    }

    public void setSqlfilename(String sqlfilename) {
        this.sqlfilename = sqlfilename;
        this.sqltext = "";
        this.sqlInputStream = null;
    }

    public String getSqlfilename() {
        return this.sqlfilename;
    }

    public void setSqlInputStream(InputStream sqlInputStream) {
        if (sqlInputStream instanceof BufferedInputStream) {
            this.sqlInputStream = (BufferedInputStream)sqlInputStream;
        } else {
            this.sqlInputStream = new BufferedInputStream(sqlInputStream);
        }

        this.sqlfilename = "";
        this.sqltext = "";
    }

    public InputStream getSqlInputStream() {
        return this.sqlInputStream;
    }

    public ArrayList<TSyntaxError> getSyntaxErrors() {
        return this.syntaxErrors;
    }

    public EDbVendor getDbVendor() {
        return this.dbVendor;
    }

    /** @deprecated */
    public void setEnablePartialParsing(boolean enablePartialParsing) {
        this.enablePartialParsing = enablePartialParsing;
    }

    /** @deprecated */
    private boolean isEnablePartialParsing() {
        return this.enablePartialParsing;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getMachineId() {
        return machineId;
    }

    public static String getLicenseMessage() {
        return licenseMessage;
    }

    public static String getLicenseType() {
        return licenseType;
    }

    public TCustomLexer getFlexer() {
        return this.flexer;
    }

    public void setTokenHandle(ITokenHandle tokenHandle) {
        this.tokenHandle = tokenHandle;
    }

    /** @deprecated */
    public void setMetaDatabase(IMetaDatabase metaDatabase) {
        this.metaDatabase = metaDatabase;
    }

    /** @deprecated */
    public IMetaDatabase getMetaDatabase() {
        return this.metaDatabase;
    }

    public void freeParseTable() {
        this.flexer.yystack = null;
        this.flexer.yytextbuf = null;
        this.flexer.buf = null;
    }

    public TGSqlParser(EDbVendor pdbvendor) {
        this.dbVendor = pdbvendor;
        this.sqltext = "";
        this.sqlfilename = "";
        this.delimiterchar = ';';
        this.defaultDelimiterStr = ";";
        switch(pdbvendor) {
            case dbvmssql:
            case dbvaccess:
            case dbvazuresql:
                this.flexer = new TLexerMssql();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserMssqlSql((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvsybase:
                this.flexer = new TLexerSybase();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserSybase((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                this.enablePartialParsing = false;
                break;
            case dbvinformix:
                this.flexer = new TLexerInformix();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserInformix((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                this.enablePartialParsing = false;
                break;
            case dbvoracle:
                this.delimiterchar = '/';
                this.flexer = new TLexerOracle();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserOracleSql((TSourceTokenList)null);
                this.fplsqlparser = new TParserOraclePLSql((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                this.fplsqlparser.lexer = this.flexer;
                this.fplsqlparser.getNf().setGsqlParser(this);
                break;
            case dbvdb2:
                this.delimiterchar = '@';
                this.flexer = new TLexerDb2();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserDb2Sql((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvmysql:
                this.delimiterchar = '$';
                this.defaultDelimiterStr = "$";
                this.flexer = new TLexerMysql();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserMysqlSql((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvteradata:
                this.delimiterchar = '/';
                this.flexer = new TLexerTeradata();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserTeradata((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvpostgresql:
                this.delimiterchar = '/';
                this.flexer = new TLexerPostgresql();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserPostgresql((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvredshift:
                this.delimiterchar = '/';
                this.flexer = new TLexerRedshift();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserRedshift((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvgreenplum:
                this.delimiterchar = '/';
                this.flexer = new TLexerGreenplum();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserGreenplum((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvmdx:
                this.delimiterchar = ';';
                this.flexer = new TLexerMdx();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserMdx((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvnetezza:
                this.delimiterchar = ';';
                this.flexer = new TLexerNetezza();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserNetezza((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvhive:
                this.delimiterchar = ';';
                this.flexer = new TLexerHive();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserHive((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvimpala:
                this.delimiterchar = ';';
                this.flexer = new TLexerImpala();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserImpala((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvhana:
                this.delimiterchar = ';';
                this.flexer = new TLexerHana();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserHana((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvdax:
                this.delimiterchar = ';';
                this.flexer = new TLexerDax();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserDax((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvodbc:
                this.delimiterchar = ';';
                this.flexer = new TLexerOdbc();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserOdbc((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvvertica:
                this.delimiterchar = ';';
                this.flexer = new TLexerVertica();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserVertica((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvopenedge:
                this.delimiterchar = ';';
                this.flexer = new TLexerOpenedge();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserOpenedge((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvcouchbase:
                this.delimiterchar = ';';
                this.flexer = new TLexerCouchbase();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserCouchbase((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvsnowflake:
                this.delimiterchar = ';';
                this.flexer = new TLexerSnowflake();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserSnowflake((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvbigquery:
                this.delimiterchar = ';';
                this.flexer = new TLexerBigquery();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserBigquery((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvansi:
            case dbvgeneric:
                this.flexer = new TLexerMssql();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserMssqlSql((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvsoql:
                this.delimiterchar = ';';
                this.flexer = new TLexerSoql();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserSoql((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvsparksql:
                this.delimiterchar = ';';
                this.flexer = new TLexerSparksql();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserSparksql((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvathena:
                this.delimiterchar = ';';
                this.flexer = new TLexerathena();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserAthena((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvpresto:
                this.delimiterchar = ';';
                this.flexer = new TLexerPresto();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserPresto((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            case dbvtrino:
                this.delimiterchar = ';';
                this.flexer = new TLexerPresto();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserPresto((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
                break;
            default:
                this.flexer = new TLexerMssql();
                this.flexer.delimiterchar = this.delimiterchar;
                this.flexer.defaultDelimiterStr = this.defaultDelimiterStr;
                this.fparser = new TParserMssqlSql((TSourceTokenList)null);
                this.fparser.lexer = this.flexer;
        }

        this.fparser.getNf().setGsqlParser(this);
        this.sourcetokenlist = new TSourceTokenList();
        this.sourcetokenlist.setGsqlparser(this);
        this.sqlstatements = new TStatementList();
        this.sqlcmds = new TSqlCmds();
        this.sqlpluskeywordList = new HashMap();
        this.syntaxErrors = new ArrayList();
        this.errormessage = "";
        currentDBVendor = this.dbVendor;
    }

    public TSelectSqlStatement parseSubquery(String subquery) {
        return parseSubquery(this.dbVendor, subquery);
    }

    public static TSelectSqlStatement parseSubquery(EDbVendor dbVendor, String subquery) {
        TGSqlParser localParser = new TGSqlParser(dbVendor);
        localParser.sqltext = subquery;
        int iRet = localParser.doparse();
        return iRet != 0 ? null : (TSelectSqlStatement)localParser.sqlstatements.get(0);
    }

    public TExpression parseExpression(String expr) {
        return parseExpression(this.dbVendor, expr);
    }

    public static TExpression parseExpression(EDbVendor dbVendor, String expr) {
        TGSqlParser localParser = new TGSqlParser(dbVendor);
        localParser.sqltext = "select 1 from t where " + TBaseType.newline + expr;
        int iRet = localParser.doparse();
        return iRet != 0 ? null : ((TSelectSqlStatement)localParser.sqlstatements.get(0)).getWhereClause().getCondition();
    }

    public TFunctionCall parseFunctionCall(String newFunction) {
        return parseFunctionCall(this.dbVendor, newFunction);
    }

    public static TFunctionCall parseFunctionCall(EDbVendor dbVendor, String newFunction) {
        TGSqlParser localParser = new TGSqlParser(dbVendor);
        localParser.sqltext = "select" + TBaseType.newline + newFunction + TBaseType.newline + "from t";
        int iRet = localParser.doparse();
        return iRet != 0 ? null : ((TSelectSqlStatement)localParser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0).getExpr().getFunctionCall();
    }

    public TObjectName parseObjectName(String newObjectName) {
        return parseObjectName(this.dbVendor, newObjectName);
    }

    public static TObjectName parseObjectName(EDbVendor dbVendor, String newObjectName) {
        TGSqlParser localParser = new TGSqlParser(dbVendor);
        localParser.sqltext = "select" + TBaseType.newline + newObjectName + TBaseType.newline + "from t";
        int iRet = localParser.doparse();
        if (iRet != 0) {
            return null;
        } else {
            TObjectName lcResult = null;
            TExpression e = ((TSelectSqlStatement)localParser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0).getExpr();
            switch(e.getExpressionType()) {
                case simple_object_name_t:
                    lcResult = e.getObjectOperand();
                    break;
                case simple_constant_t:
                    lcResult = new TObjectName();
                    lcResult.init(e.getConstantOperand().getValueToken());
            }

            return lcResult;
        }
    }

    public TConstant parseConstant(String newConstant) {
        return parseConstant(this.dbVendor, newConstant);
    }

    public static TConstant parseConstant(EDbVendor dbVendor, String newConstant) {
        TGSqlParser localParser = new TGSqlParser(dbVendor);
        localParser.sqltext = "select" + TBaseType.newline + newConstant + TBaseType.newline + "from t";
        int iRet = localParser.doparse();
        return iRet != 0 ? null : ((TSelectSqlStatement)localParser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0).getExpr().getConstantOperand();
    }

    public int getErrorCount() {
        return this.syntaxErrors.size();
    }

    public String getErrormessage() {
        String s = "";
        String hint = "Syntax error";

        for(int i = 0; i < this.syntaxErrors.size(); ++i) {
            TSyntaxError t = (TSyntaxError)this.syntaxErrors.get(i);
            if (t.hint.length() > 0) {
                hint = t.hint;
            }

            s = s + hint + "(" + t.errorno + ") near: " + t.tokentext;
            s = s + "(" + t.lineNo;
            s = s + "," + t.columnNo + ")";
            if (i != this.syntaxErrors.size() - 1) {
                s = s + TBaseType.linebreak;
            }
        }

        if (this.errormessage.length() > 0) {
            s = this.errormessage + TBaseType.linebreak + s;
        }

        return s;
    }

    public int checkSyntax() {
        return this.doparse();
    }

    public int parse() {
        return this.doparse();
    }

    void setdelimiterchar(char ch) {
        this.delimiterchar = ch;
        this.flexer.delimiterchar = ch;
    }

    boolean includesqlstatementtype(ESqlStatementType search, ESqlStatementType[] src) {
        boolean ret = false;

        for(int i = 0; i < src.length; ++i) {
            if (src[i] == search) {
                ret = true;
                break;
            }
        }

        return ret;
    }

    private int getfileEncodingType(BufferedInputStream fr) {
        byte ret = 0;

        try {
            byte[] bom = new byte[4];
            fr.mark(bom.length + 1);
            fr.read(bom, 0, bom.length);
            if ((bom[0] != -1 || bom[1] != -2) && (bom[0] != -2 || bom[1] != -1)) {
                if (bom[0] == -17 && bom[1] == -69 && bom[2] == -65) {
                    ret = 3;
                }
            } else {
                ret = 1;
                if (bom[2] == -1 && bom[3] == -2 || bom[2] == -2 && bom[3] == -1) {
                    ret = 2;
                }
            }

            fr.reset();
        } catch (FileNotFoundException var4) {
        } catch (IOException var5) {
        }

        return ret;
    }

    private int getfileEncodingType(String fn) {
        int ret = 0;

        try {
            FileInputStream fr = new FileInputStream(this.sqlfilename);
            ret = this.getfileEncodingType(new BufferedInputStream(fr));
            fr.close();
        } catch (FileNotFoundException var4) {
        } catch (IOException var5) {
        }

        return ret;
    }

    int readsql() {
        int ret = 0;
        this.syntaxErrors.clear();
        licenseType = "dist";
        userName = "dist";

        try {
            if (this.finputstream != null) {
                this.finputstream.close();
            }

            if (this.flexer == null) {
                ret = -1;
                this.errormessage = "requested database not supported:" + this.dbVendor.toString();
                return ret;
            }

            if (this.flexer.yyinput != null) {
                this.flexer.yyinput.close();
            }
        } catch (IOException var10) {
            ret = -1;
            this.errormessage = "requested database not supported";
        }

        if (this.sqltext.length() > 0) {
            this.finputstream = new BufferedReader(new StringReader(this.sqltext));
            // if (this.sqltext.length() > 10000) {
            //     this.errormessage = "trial version can only process query with size less than 10000 characters, and expired after 90 days after first usage.";
            //     ret = -1;
            // }
        } else {
            int encodingtype;
            if (this.sqlfilename.length() > 0) {
                try {
                    encodingtype = this.getfileEncodingType(this.sqlfilename);
                    this.streamFromSqlFile = new FileInputStream(this.sqlfilename);
                    if (encodingtype == 1) {
                        this.sqlStreamReader = new InputStreamReader(this.streamFromSqlFile, "UTF-16");
                    } else if (encodingtype == 2) {
                        this.sqlStreamReader = new InputStreamReader(this.streamFromSqlFile, "UTF-32");
                    } else if (encodingtype == 3) {
                        this.sqlStreamReader = new InputStreamReader(this.streamFromSqlFile, "UTF-8");
                    } else {
                        this.sqlStreamReader = new InputStreamReader(this.streamFromSqlFile, Charset.defaultCharset().name());
                    }

                    this.finputstream = new BufferedReader(this.sqlStreamReader);
                    if (encodingtype == 3) {
                        this.finputstream.skip(1L);
                    }

                    File file = new File(this.sqlfilename);
                    if (file.exists() && file.isFile()) {
                        // if (file.length() > 10000L) {
                        //     this.errormessage = "trial version can only process query in file with size less than 10000 bytes, and expired after 90 days after first usage.";
                        //     ret = -1;
                        // }
                    } else {
                        ret = -1;
                        this.errormessage = "not a valid sql file.";
                    }
                } catch (FileNotFoundException var11) {
                    ret = -1;
                    this.errormessage = var11.toString();
                } catch (UnsupportedEncodingException var12) {
                    var12.printStackTrace();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            } else if (this.sqlInputStream != null) {
                encodingtype = this.getfileEncodingType(this.sqlInputStream);
                InputStream fr = this.sqlInputStream;
                InputStreamReader isr = null;

                try {
                    if (encodingtype == 1) {
                        isr = new InputStreamReader(fr, "UTF-16");
                    } else if (encodingtype == 2) {
                        isr = new InputStreamReader(fr, "UTF-32");
                    } else if (encodingtype == 3) {
                        isr = new InputStreamReader(fr, "UTF-8");
                    } else {
                        isr = new InputStreamReader(fr, Charset.defaultCharset().name());
                    }

                    this.finputstream = new BufferedReader(isr);
                    if (encodingtype == 3) {
                        this.finputstream.skip(1L);
                    }
                } catch (FileNotFoundException var7) {
                    ret = -1;
                    this.errormessage = var7.toString();
                } catch (UnsupportedEncodingException var8) {
                    var8.printStackTrace();
                } catch (IOException var9) {
                    var9.printStackTrace();
                }

                // try {
                    // if (this.sqlInputStream.available() > 10000) {
                    //     this.errormessage = "trial version can only process query with size less than 10000 characters, and expired after 90 days after first usage.";
                    //     ret = -1;
                    // }
                // } catch (IOException var6) {
                //     var6.printStackTrace();
                // }
            }
        }

        if (this.finputstream == null) {
            ret = -1;
        }

        if (ret == 0) {
            this.flexer.yyinput = this.finputstream;
        }

        this.sourcetokenlist.clear();
        this.sourcetokenlist.curpos = -1;
        return ret;
    }

    TSourceToken getanewsourcetoken() {
        TSourceToken pst = null;

        while(true) {
            pst = new TSourceToken("");
            if (this.flexer.yylexwrap(pst) == 0) {
                pst = null;
                break;
            }

            pst.setDbvendor(this.dbVendor);
            pst.tokenstatus = ETokenStatus.tsoriginal;
            if (pst.tokentype == ETokenType.ttreturn) {
                pst.astext = this.towinlinebreak(pst.astext);
            }

            TSourceToken prevst;
            if (pst.tokentype == ETokenType.ttwhitespace && this.sourcetokenlist.curpos >= 0) {
                prevst = this.sourcetokenlist.get(this.sourcetokenlist.curpos);
                if (prevst.tokentype == ETokenType.ttreturn) {
                    prevst.astext = prevst.astext + pst.astext;
                    continue;
                }
            }

            if (pst.tokentype != ETokenType.ttreturn || this.sourcetokenlist.curpos < 0) {
                break;
            }

            prevst = this.sourcetokenlist.get(this.sourcetokenlist.curpos);
            if (prevst.tokentype != ETokenType.ttreturn) {
                if (prevst.tokentype == ETokenType.ttwhitespace) {
                }
                break;
            }

            prevst.astext = prevst.astext + pst.astext;
        }

        if (pst != null) {
            pst.container = this.sourcetokenlist;
            ++this.sourcetokenlist.curpos;
            pst.posinlist = this.sourcetokenlist.curpos;
            if (this.tokenHandle != null) {
                this.tokenHandle.processToken(pst);
            }
        }

        return pst;
    }

    String towinlinebreak(String s) {
        return s;
    }

    void checkconstarinttoken(TSourceToken lcprevtoken) {
        TSourceTokenList lcStList = lcprevtoken.container;
        if (TBaseType.assigned(lcStList)) {
            TSourceToken lcPPToken = lcStList.nextsolidtoken(lcprevtoken.posinlist, -2, false);
            if (TBaseType.assigned(lcPPToken) && lcPPToken.tokencode == this.flexer.getkeywordvalue("constraint")) {
                lcPPToken.tokencode = 298;
            }
        }

    }

    TSourceToken getprevtoken(TSourceToken ptoken) {
        TSourceTokenList lcstlist = ptoken.container;
        if (TBaseType.assigned(lcstlist) && ptoken.posinlist > 0 && lcstlist.size() > ptoken.posinlist - 1) {
            return lcstlist.get(ptoken.posinlist - 1).tokentype != ETokenType.ttwhitespace && lcstlist.get(ptoken.posinlist - 1).tokentype != ETokenType.ttreturn && lcstlist.get(ptoken.posinlist - 1).tokentype != ETokenType.ttsimplecomment && lcstlist.get(ptoken.posinlist - 1).tokentype != ETokenType.ttbracketedcomment ? lcstlist.get(ptoken.posinlist - 1) : lcstlist.nextsolidtoken(ptoken.posinlist - 1, -1, false);
        } else {
            return null;
        }
    }

    void doinformixtexttotokenlist() {
        TSourceToken lcprevtoken = null;
        int lcsteps = 0;
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            int yychar = asourcetoken.tokencode;
            boolean lcinopenrowset = false;
            int lcnested = 0;

            while(yychar > 0) {
                if (asourcetoken.tokencode == 464) {
                    lcinopenrowset = true;
                    lcnested = 0;
                } else if (asourcetoken.tokentype == ETokenType.ttleftparenthesis) {
                    if (lcsteps > 0 && TBaseType.assigned(lcprevtoken)) {
                        if (lcprevtoken.tokencode == 513) {
                            lcprevtoken.tokencode = 299;
                        } else if (lcprevtoken.tokencode == 516) {
                            lcprevtoken.tokencode = 297;
                        } else if (lcprevtoken.tokencode == 514) {
                            lcprevtoken.tokencode = 300;
                        } else if (lcprevtoken.tokencode == 338) {
                            lcprevtoken.tokencode = 295;
                        }

                        lcprevtoken = null;
                        lcsteps = 0;
                    }

                    if (lcinopenrowset) {
                        ++lcnested;
                    }
                } else if (asourcetoken.tokentype == ETokenType.ttrightparenthesis) {
                    if (lcinopenrowset) {
                        if (lcnested > 0) {
                            --lcnested;
                        }

                        if (lcnested == 0) {
                            lcinopenrowset = false;
                        }
                    }
                } else if (asourcetoken.tokentype == ETokenType.ttsemicolon) {
                    if (lcinopenrowset) {
                        asourcetoken.tokentype = ETokenType.ttsemicolon2;
                    } else {
                        TSourceToken lctoken2 = asourcetoken.searchToken(351, -1);
                        if (lctoken2 != null) {
                            asourcetoken.tokencode = 531;
                            asourcetoken.tokentype = ETokenType.ttsemicolon3;
                        } else {
                            lctoken2 = asourcetoken.searchToken(456, -1);
                            if (lctoken2 == null) {
                                lctoken2 = asourcetoken.searchToken(462, -1);
                            }

                            if (lctoken2 != null) {
                                TSourceToken lctoken3 = asourcetoken.searchToken(351, -2);
                                if (lctoken3 != null) {
                                    asourcetoken.tokencode = 531;
                                    asourcetoken.tokentype = ETokenType.ttsemicolon3;
                                }
                            }
                        }
                    }
                } else {
                    TSourceToken lctoken;
                    if (asourcetoken.tokentype == ETokenType.ttperiod) {
                        lctoken = this.getprevtoken(asourcetoken);
                        if (TBaseType.assigned(lctoken) && lctoken.tokencode == 463) {
                            lctoken.tokencode = 264;
                            lctoken.tokentype = ETokenType.ttidentifier;
                        }
                    } else if (asourcetoken.tokencode == 308) {
                        lctoken = this.getprevtoken(asourcetoken);
                        if (TBaseType.assigned(lctoken) && lctoken.tokencode == 473) {
                            lctoken.tokencode = 296;
                        }
                    } else if (asourcetoken.tokencode == 512) {
                        lctoken = this.getprevtoken(asourcetoken);
                        if (TBaseType.assigned(lctoken) && lctoken.tokencode == 481) {
                            lctoken.tokencode = 537;
                        }
                    } else if (asourcetoken.tokencode == 463) {
                        boolean iskeywordgo = true;
                        lctoken = this.getprevtoken(asourcetoken);
                        if (TBaseType.assigned(lctoken) && lctoken.lineNo == asourcetoken.lineNo) {
                            iskeywordgo = false;
                        }

                        if (iskeywordgo) {
                            lcinopenrowset = false;
                            lcnested = 0;
                            lcprevtoken = asourcetoken;
                        } else {
                            asourcetoken.tokencode = 264;
                            asourcetoken.tokentype = ETokenType.ttidentifier;
                        }
                    } else if (asourcetoken.tokencode == 513) {
                        lcsteps = 2;
                        lcprevtoken = asourcetoken;
                    } else if (asourcetoken.tokencode == 516) {
                        lcsteps = 2;
                        lcprevtoken = asourcetoken;
                    } else if (asourcetoken.tokencode == 514) {
                        lcsteps = 1;
                        lcprevtoken = asourcetoken;
                    } else if (asourcetoken.issolidtoken() && lcsteps > 0 && !TBaseType.mysametext("clustered", asourcetoken.astext) && !TBaseType.mysametext("nonclustered", asourcetoken.astext)) {
                        --lcsteps;
                    }
                }

                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken != null) {
                    yychar = asourcetoken.tokencode;
                } else {
                    yychar = 0;
                }
            }

        }
    }

    void domssqlsqltexttotokenlist() {
        TSourceToken lcprevtoken = null;
        int lcsteps = 0;
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            int yychar = asourcetoken.tokencode;
            boolean lcinopenrowset = false;
            int lcnested = 0;

            while(yychar > 0) {
                if (asourcetoken.tokencode == 464) {
                    lcinopenrowset = true;
                    lcnested = 0;
                } else if (asourcetoken.tokentype == ETokenType.ttleftparenthesis) {
                    if (lcsteps > 0 && TBaseType.assigned(lcprevtoken)) {
                        if (lcprevtoken.tokencode == 513) {
                            lcprevtoken.tokencode = 299;
                            this.checkconstarinttoken(lcprevtoken);
                        } else if (lcprevtoken.tokencode == 516) {
                            lcprevtoken.tokencode = 297;
                            this.checkconstarinttoken(lcprevtoken);
                        } else if (lcprevtoken.tokencode == 514) {
                            lcprevtoken.tokencode = 300;
                            this.checkconstarinttoken(lcprevtoken);
                        }

                        lcprevtoken = null;
                        lcsteps = 0;
                    }

                    if (lcinopenrowset) {
                        ++lcnested;
                    }
                } else if (asourcetoken.tokentype == ETokenType.ttrightparenthesis) {
                    if (lcinopenrowset) {
                        if (lcnested > 0) {
                            --lcnested;
                        }

                        if (lcnested == 0) {
                            lcinopenrowset = false;
                        }
                    }
                } else {
                    TSourceToken lctoken;
                    if (asourcetoken.tokentype == ETokenType.ttsemicolon) {
                        if (lcinopenrowset) {
                            asourcetoken.tokentype = ETokenType.ttsemicolon2;
                        } else {
                            TSourceToken lctoken2 = asourcetoken.searchToken(351, -1);
                            if (lctoken2 != null) {
                                asourcetoken.tokencode = 531;
                                asourcetoken.tokentype = ETokenType.ttsemicolon3;
                            } else {
                                lctoken2 = asourcetoken.searchToken(456, -1);
                                if (lctoken2 == null) {
                                    lctoken2 = asourcetoken.searchToken(462, -1);
                                }

                                if (lctoken2 != null) {
                                    TSourceToken lctoken3 = asourcetoken.searchToken(351, -2);
                                    if (lctoken3 != null) {
                                        asourcetoken.tokencode = 531;
                                        asourcetoken.tokentype = ETokenType.ttsemicolon3;
                                    }
                                }
                            }
                        }

                        lctoken = this.getprevtoken(asourcetoken);
                        if (lctoken != null && lctoken.tokentype == ETokenType.ttsemicolon) {
                            asourcetoken.tokencode = 259;
                        }
                    } else if (asourcetoken.tokentype == ETokenType.ttperiod) {
                        lctoken = this.getprevtoken(asourcetoken);
                        if (TBaseType.assigned(lctoken) && lctoken.tokencode == 463) {
                            lctoken.tokencode = 264;
                            lctoken.tokentype = ETokenType.ttidentifier;
                        }
                    } else if (asourcetoken.tokencode == 308) {
                        lctoken = this.getprevtoken(asourcetoken);
                        if (TBaseType.assigned(lctoken) && lctoken.tokencode == 473) {
                            lctoken.tokencode = 296;
                        }
                    } else if (asourcetoken.tokencode == 463) {
                        boolean iskeywordgo = true;
                        lctoken = this.getprevtoken(asourcetoken);
                        if (TBaseType.assigned(lctoken) && lctoken.lineNo == asourcetoken.lineNo) {
                            iskeywordgo = false;
                        }

                        if (iskeywordgo) {
                            lcinopenrowset = false;
                            lcnested = 0;
                            lcprevtoken = asourcetoken;
                        } else {
                            asourcetoken.tokencode = 264;
                            asourcetoken.tokentype = ETokenType.ttidentifier;
                        }
                    } else if (asourcetoken.tokencode == 513) {
                        lcsteps = 2;
                        lcprevtoken = asourcetoken;
                    } else if (asourcetoken.tokencode == 516) {
                        lcsteps = 2;
                        lcprevtoken = asourcetoken;
                    } else if (asourcetoken.tokencode == 514) {
                        lcsteps = 1;
                        lcprevtoken = asourcetoken;
                    } else if (asourcetoken.issolidtoken() && lcsteps > 0 && !TBaseType.mysametext("clustered", asourcetoken.astext) && !TBaseType.mysametext("nonclustered", asourcetoken.astext) && !TBaseType.mysametext("hash", asourcetoken.astext)) {
                        --lcsteps;
                    }
                }

                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken != null) {
                    yychar = asourcetoken.tokencode;
                } else {
                    yychar = 0;
                }
            }

        }
    }

    void dosybasesqltexttotokenlist() {
        TSourceToken lcprevtoken = null;
        int lcsteps = 0;
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            int yychar = asourcetoken.tokencode;
            boolean lcinopenrowset = false;
            int lcnested = 0;

            while(yychar > 0) {
                if (asourcetoken.tokencode == 464) {
                    lcinopenrowset = true;
                    lcnested = 0;
                } else if (asourcetoken.tokentype == ETokenType.ttleftparenthesis) {
                    if (lcsteps > 0 && TBaseType.assigned(lcprevtoken)) {
                        if (lcprevtoken.tokencode == 513) {
                            lcprevtoken.tokencode = 299;
                            this.checkconstarinttoken(lcprevtoken);
                        } else if (lcprevtoken.tokencode == 516) {
                            lcprevtoken.tokencode = 297;
                            this.checkconstarinttoken(lcprevtoken);
                        } else if (lcprevtoken.tokencode == 514) {
                            lcprevtoken.tokencode = 300;
                            this.checkconstarinttoken(lcprevtoken);
                        }

                        lcprevtoken = null;
                        lcsteps = 0;
                    }

                    if (lcinopenrowset) {
                        ++lcnested;
                    }
                } else if (asourcetoken.tokentype == ETokenType.ttrightparenthesis) {
                    if (lcinopenrowset) {
                        if (lcnested > 0) {
                            --lcnested;
                        }

                        if (lcnested == 0) {
                            lcinopenrowset = false;
                        }
                    }
                } else if (asourcetoken.tokentype == ETokenType.ttsemicolon) {
                    if (lcinopenrowset) {
                        asourcetoken.tokentype = ETokenType.ttsemicolon2;
                    } else {
                        TSourceToken lctoken2 = asourcetoken.searchToken(351, -1);
                        if (lctoken2 != null) {
                            asourcetoken.tokencode = 531;
                            asourcetoken.tokentype = ETokenType.ttsemicolon3;
                        } else {
                            lctoken2 = asourcetoken.searchToken(456, -1);
                            if (lctoken2 == null) {
                                lctoken2 = asourcetoken.searchToken(462, -1);
                            }

                            if (lctoken2 != null) {
                                TSourceToken lctoken3 = asourcetoken.searchToken(351, -2);
                                if (lctoken3 != null) {
                                    asourcetoken.tokencode = 531;
                                    asourcetoken.tokentype = ETokenType.ttsemicolon3;
                                }
                            }
                        }
                    }
                } else {
                    TSourceToken lctoken;
                    if (asourcetoken.tokentype == ETokenType.ttperiod) {
                        lctoken = this.getprevtoken(asourcetoken);
                        if (TBaseType.assigned(lctoken) && lctoken.tokencode == 463) {
                            lctoken.tokencode = 264;
                            lctoken.tokentype = ETokenType.ttidentifier;
                        }
                    } else if (asourcetoken.tokencode == 308) {
                        lctoken = this.getprevtoken(asourcetoken);
                        if (TBaseType.assigned(lctoken) && lctoken.tokencode == 473) {
                            lctoken.tokencode = 296;
                        }
                    } else if (asourcetoken.tokencode == 560) {
                        lctoken = this.getprevtoken(asourcetoken);
                        if (TBaseType.assigned(lctoken) && lctoken.tokencode == 559) {
                            lctoken.tokencode = 287;
                        }
                    } else if (asourcetoken.tokencode == 304) {
                        lctoken = this.getprevtoken(asourcetoken);
                        if (TBaseType.assigned(lctoken) && (lctoken.tokencode == 321 || lctoken.tokencode == 319 || lctoken.tokencode == 320 || lctoken.tokencode == 305)) {
                            asourcetoken.tokencode = 288;
                        }
                    } else if (asourcetoken.tokencode == 463) {
                        boolean iskeywordgo = true;
                        lctoken = this.getprevtoken(asourcetoken);
                        if (TBaseType.assigned(lctoken) && lctoken.lineNo == asourcetoken.lineNo) {
                            iskeywordgo = false;
                        }

                        if (iskeywordgo) {
                            lcinopenrowset = false;
                            lcnested = 0;
                            lcprevtoken = asourcetoken;
                        } else {
                            asourcetoken.tokencode = 264;
                            asourcetoken.tokentype = ETokenType.ttidentifier;
                        }
                    } else if (asourcetoken.tokencode == 513) {
                        lcsteps = 2;
                        lcprevtoken = asourcetoken;
                    } else if (asourcetoken.tokencode == 516) {
                        lcsteps = 2;
                        lcprevtoken = asourcetoken;
                    } else if (asourcetoken.tokencode == 514) {
                        lcsteps = 1;
                        lcprevtoken = asourcetoken;
                    } else if (asourcetoken.issolidtoken() && lcsteps > 0 && !TBaseType.mysametext("clustered", asourcetoken.astext) && !TBaseType.mysametext("nonclustered", asourcetoken.astext)) {
                        --lcsteps;
                    }
                }

                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken != null) {
                    yychar = asourcetoken.tokencode;
                } else {
                    yychar = 0;
                }
            }

        }
    }

    boolean IsValidPlaceForDivToSqlplusCmd(TSourceTokenList pstlist, int pPos) {
        boolean ret = false;
        if (pPos > 0 && pPos <= pstlist.size() - 1) {
            TSourceToken lcst = pstlist.get(pPos - 1);
            if (lcst.tokentype != ETokenType.ttreturn) {
                return ret;
            } else {
                if (lcst.astext.charAt(lcst.astext.length() - 1) != ' ') {
                    ret = true;
                }

                return ret;
            }
        } else {
            return ret;
        }
    }

    boolean isvalidsqlpluscmdInPostgresql(String astr) {
        return false;
    }

    TSourceToken getprevsolidtoken(TSourceToken ptoken) {
        TSourceToken ret = null;
        TSourceTokenList lctokenlist = ptoken.container;
        if (lctokenlist != null && ptoken.posinlist > 0 && lctokenlist.size() > ptoken.posinlist - 1) {
            if (lctokenlist.get(ptoken.posinlist - 1).tokentype != ETokenType.ttwhitespace && lctokenlist.get(ptoken.posinlist - 1).tokentype != ETokenType.ttreturn && lctokenlist.get(ptoken.posinlist - 1).tokentype != ETokenType.ttsimplecomment && lctokenlist.get(ptoken.posinlist - 1).tokentype != ETokenType.ttbracketedcomment) {
                ret = lctokenlist.get(ptoken.posinlist - 1);
            } else {
                ret = lctokenlist.nextsolidtoken(ptoken.posinlist - 1, -1, false);
            }
        }

        return ret;
    }

    void doredshifttexttotokenlist() {
        boolean insqlpluscmd = false;
        boolean isvalidplace = true;
        boolean waitingreturnforfloatdiv = false;
        boolean waitingreturnforsemicolon = false;
        boolean continuesqlplusatnewline = false;
        TSourceToken lct = null;
        TSourceToken prevst = null;
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            int yychar = asourcetoken.tokencode;

            while(yychar > 0) {
                label93: {
                    this.sourcetokenlist.add(asourcetoken);
                    switch(yychar) {
                        case 257:
                        case 258:
                        case 259:
                            if (insqlpluscmd) {
                                asourcetoken.insqlpluscmd = true;
                            }
                            break label93;
                        case 260:
                            if (insqlpluscmd) {
                                insqlpluscmd = false;
                                isvalidplace = true;
                                if (continuesqlplusatnewline) {
                                    insqlpluscmd = true;
                                    isvalidplace = false;
                                    asourcetoken.insqlpluscmd = true;
                                }
                            }

                            if (waitingreturnforsemicolon) {
                                isvalidplace = true;
                            }

                            if (waitingreturnforfloatdiv) {
                                isvalidplace = true;
                                lct.tokencode = 273;
                                if (lct.tokentype != ETokenType.ttslash) {
                                    lct.tokentype = ETokenType.ttsqlpluscmd;
                                }
                            }

                            this.flexer.insqlpluscmd = insqlpluscmd;
                            break label93;
                    }

                    continuesqlplusatnewline = false;
                    waitingreturnforsemicolon = false;
                    waitingreturnforfloatdiv = false;
                    if (insqlpluscmd) {
                        asourcetoken.insqlpluscmd = true;
                        if (asourcetoken.astext.equalsIgnoreCase("-")) {
                            continuesqlplusatnewline = true;
                        }
                    } else {
                        if (asourcetoken.tokentype == ETokenType.ttsemicolon) {
                            waitingreturnforsemicolon = true;
                        }

                        if (asourcetoken.tokentype == ETokenType.ttslash && (isvalidplace || this.IsValidPlaceForDivToSqlplusCmd(this.sourcetokenlist, asourcetoken.posinlist))) {
                            lct = asourcetoken;
                            waitingreturnforfloatdiv = true;
                        }

                        if (isvalidplace && this.isvalidsqlpluscmdInPostgresql(asourcetoken.astext)) {
                            asourcetoken.tokencode = 273;
                            if (asourcetoken.tokentype != ETokenType.ttslash) {
                                asourcetoken.tokentype = ETokenType.ttsqlpluscmd;
                            }

                            insqlpluscmd = true;
                            this.flexer.insqlpluscmd = insqlpluscmd;
                        }
                    }

                    isvalidplace = false;
                    if (asourcetoken.tokencode == 534) {
                        TSourceToken stPercent = asourcetoken.searchToken(37, -1);
                        if (stPercent != null) {
                            stPercent.tokencode = 296;
                        }
                    }
                }

                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken != null) {
                    yychar = asourcetoken.tokencode;
                } else {
                    yychar = 0;
                    if (waitingreturnforfloatdiv) {
                        lct.tokencode = 273;
                        if (lct.tokentype != ETokenType.ttslash) {
                            lct.tokentype = ETokenType.ttsqlpluscmd;
                        }
                    }
                }

                if (yychar == 0 && prevst != null) {
                }
            }

        }
    }

    void dopostgresqltexttotokenlist() {
        boolean insqlpluscmd = false;
        boolean isvalidplace = true;
        boolean waitingreturnforfloatdiv = false;
        boolean waitingreturnforsemicolon = false;
        boolean continuesqlplusatnewline = false;
        TSourceToken lct = null;
        TSourceToken prevst = null;
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            int yychar = asourcetoken.tokencode;

            while(yychar > 0) {
                label154: {
                    this.sourcetokenlist.add(asourcetoken);
                    switch(yychar) {
                        case 257:
                        case 258:
                        case 259:
                            if (insqlpluscmd) {
                                asourcetoken.insqlpluscmd = true;
                            }
                            break label154;
                        case 260:
                            if (insqlpluscmd) {
                                insqlpluscmd = false;
                                isvalidplace = true;
                                if (continuesqlplusatnewline) {
                                    insqlpluscmd = true;
                                    isvalidplace = false;
                                    asourcetoken.insqlpluscmd = true;
                                }
                            }

                            if (waitingreturnforsemicolon) {
                                isvalidplace = true;
                            }

                            if (waitingreturnforfloatdiv) {
                                isvalidplace = true;
                                lct.tokencode = 273;
                                if (lct.tokentype != ETokenType.ttslash) {
                                    lct.tokentype = ETokenType.ttsqlpluscmd;
                                }
                            }

                            this.flexer.insqlpluscmd = insqlpluscmd;
                            break label154;
                    }

                    continuesqlplusatnewline = false;
                    waitingreturnforsemicolon = false;
                    waitingreturnforfloatdiv = false;
                    if (insqlpluscmd) {
                        asourcetoken.insqlpluscmd = true;
                        if (asourcetoken.astext.equalsIgnoreCase("-")) {
                            continuesqlplusatnewline = true;
                        }
                    } else {
                        if (asourcetoken.tokentype == ETokenType.ttsemicolon) {
                            waitingreturnforsemicolon = true;
                        }

                        if (asourcetoken.tokentype == ETokenType.ttslash && (isvalidplace || this.IsValidPlaceForDivToSqlplusCmd(this.sourcetokenlist, asourcetoken.posinlist))) {
                            lct = asourcetoken;
                            waitingreturnforfloatdiv = true;
                        }

                        if (isvalidplace && this.isvalidsqlpluscmdInPostgresql(asourcetoken.astext)) {
                            asourcetoken.tokencode = 273;
                            if (asourcetoken.tokentype != ETokenType.ttslash) {
                                asourcetoken.tokentype = ETokenType.ttsqlpluscmd;
                            }

                            insqlpluscmd = true;
                            this.flexer.insqlpluscmd = insqlpluscmd;
                        }
                    }

                    isvalidplace = false;
                    if (prevst != null) {
                        if (prevst.tokencode == 345 && asourcetoken.tokencode != this.flexer.getkeywordvalue("JOIN")) {
                            prevst.tokencode = 264;
                        }

                        if (prevst.tokencode == 321 && asourcetoken.tokencode == this.flexer.getkeywordvalue("DEFERRABLE")) {
                            prevst.tokencode = this.flexer.getkeywordvalue("NOT_DEFERRABLE");
                        }
                    }

                    if (asourcetoken.tokencode == 345) {
                        prevst = asourcetoken;
                    } else if (asourcetoken.tokencode == 321) {
                        prevst = asourcetoken;
                    } else {
                        prevst = null;
                    }

                    if (asourcetoken.tokencode == this.flexer.getkeywordvalue("DIRECT_LOAD") || asourcetoken.tokencode == this.flexer.getkeywordvalue("ALL")) {
                        TSourceToken lcprevst = this.getprevsolidtoken(asourcetoken);
                        if (lcprevst != null && lcprevst.tokencode == 306) {
                            lcprevst.tokencode = 285;
                        }
                    }

                    TSourceToken stDo;
                    if (asourcetoken.tokencode == 534) {
                        stDo = asourcetoken.searchToken(532, -2);
                        if (stDo != null) {
                            stDo.tokencode = 533;
                        }
                    }

                    if (asourcetoken.tokencode == 548) {
                        stDo = asourcetoken.searchToken(37, -1);
                        if (stDo != null) {
                            stDo.tokencode = 296;
                        }
                    }

                    if (asourcetoken.tokencode == 298) {
                        stDo = asourcetoken.searchToken(61, -1);
                        if (stDo != null) {
                            asourcetoken.tokencode = 264;
                        }
                    }

                    if (asourcetoken.tokencode == 304) {
                        stDo = asourcetoken.searchToken(501, -1);
                        if (stDo != null) {
                            asourcetoken.tokencode = 551;
                        }
                    }
                }

                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken != null) {
                    yychar = asourcetoken.tokencode;
                } else {
                    yychar = 0;
                    if (waitingreturnforfloatdiv) {
                        lct.tokencode = 273;
                        if (lct.tokentype != ETokenType.ttslash) {
                            lct.tokentype = ETokenType.ttsqlpluscmd;
                        }
                    }
                }

                if (yychar == 0 && prevst != null && prevst.tokencode == 345) {
                    prevst.tokencode = 264;
                }
            }

        }
    }

    void dosnowflakesqltexttotokenlist() {
        boolean insqlpluscmd = false;
        boolean isvalidplace = true;
        boolean waitingreturnforfloatdiv = false;
        boolean waitingreturnforsemicolon = false;
        boolean continuesqlplusatnewline = false;
        TSourceToken lct = null;
        TSourceToken prevst = null;
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            int yychar = asourcetoken.tokencode;

            while(yychar > 0) {
                label111: {
                    this.sourcetokenlist.add(asourcetoken);
                    switch(yychar) {
                        case 257:
                        case 258:
                        case 259:
                            if (insqlpluscmd) {
                                asourcetoken.insqlpluscmd = true;
                            }
                            break label111;
                        case 260:
                            if (insqlpluscmd) {
                                insqlpluscmd = false;
                                isvalidplace = true;
                                if (continuesqlplusatnewline) {
                                    insqlpluscmd = true;
                                    isvalidplace = false;
                                    asourcetoken.insqlpluscmd = true;
                                }
                            }

                            if (waitingreturnforsemicolon) {
                                isvalidplace = true;
                            }

                            if (waitingreturnforfloatdiv) {
                                isvalidplace = true;
                                lct.tokencode = 273;
                                if (lct.tokentype != ETokenType.ttslash) {
                                    lct.tokentype = ETokenType.ttsqlpluscmd;
                                }
                            }

                            this.flexer.insqlpluscmd = insqlpluscmd;
                            break label111;
                    }

                    continuesqlplusatnewline = false;
                    waitingreturnforsemicolon = false;
                    waitingreturnforfloatdiv = false;
                    if (insqlpluscmd) {
                        asourcetoken.insqlpluscmd = true;
                        if (asourcetoken.astext.equalsIgnoreCase("-")) {
                            continuesqlplusatnewline = true;
                        }
                    } else {
                        if (asourcetoken.tokentype == ETokenType.ttsemicolon) {
                            waitingreturnforsemicolon = true;
                        }

                        if (asourcetoken.tokentype == ETokenType.ttslash && (isvalidplace || this.IsValidPlaceForDivToSqlplusCmd(this.sourcetokenlist, asourcetoken.posinlist))) {
                            lct = asourcetoken;
                            waitingreturnforfloatdiv = true;
                        }

                        if (isvalidplace && this.isvalidsqlpluscmdInPostgresql(asourcetoken.astext)) {
                            asourcetoken.tokencode = 273;
                            if (asourcetoken.tokentype != ETokenType.ttslash) {
                                asourcetoken.tokentype = ETokenType.ttsqlpluscmd;
                            }

                            insqlpluscmd = true;
                            this.flexer.insqlpluscmd = insqlpluscmd;
                        }
                    }

                    isvalidplace = false;
                    if (prevst != null) {
                        if (prevst.tokencode == 345 && asourcetoken.tokencode != this.flexer.getkeywordvalue("JOIN")) {
                            prevst.tokencode = 264;
                        }

                        if (prevst.tokencode == 321 && asourcetoken.tokencode == this.flexer.getkeywordvalue("DEFERRABLE")) {
                            prevst.tokencode = this.flexer.getkeywordvalue("NOT_DEFERRABLE");
                        }
                    }

                    if (asourcetoken.tokencode == 345) {
                        prevst = asourcetoken;
                    } else if (asourcetoken.tokencode == 321) {
                        prevst = asourcetoken;
                    } else {
                        prevst = null;
                    }
                }

                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken != null) {
                    yychar = asourcetoken.tokencode;
                } else {
                    yychar = 0;
                    if (waitingreturnforfloatdiv) {
                        lct.tokencode = 273;
                        if (lct.tokentype != ETokenType.ttslash) {
                            lct.tokentype = ETokenType.ttsqlpluscmd;
                        }
                    }
                }

                if (yychar == 0 && prevst != null && prevst.tokencode == 345) {
                    prevst.tokencode = 264;
                }
            }

        }
    }

    void doverticatexttotokenlist() {
        this.dopostgresqltexttotokenlist();
    }

    void docouchbasesqltexttotokenlist() {
        this.dopostgresqltexttotokenlist();
    }

    void dobigquerysqltexttotokenlist() {
        this.flexer.tmpDelimiter = "";
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            int yychar = asourcetoken.tokencode;
            this.checkMySQLCommentToken(asourcetoken);

            while(yychar > 0) {
                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken == null) {
                    break;
                }

                yychar = asourcetoken.tokencode;
            }

        }
    }

    void dosoqlsqltexttotokenlist() {
        this.domssqlsqltexttotokenlist();
    }

    void dogreenplumtexttotokenlist() {
        boolean insqlpluscmd = false;
        boolean isvalidplace = true;
        boolean waitingreturnforfloatdiv = false;
        boolean waitingreturnforsemicolon = false;
        boolean continuesqlplusatnewline = false;
        TSourceToken lct = null;
        TSourceToken prevst = null;
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            int yychar = asourcetoken.tokencode;

            while(yychar > 0) {
                label142: {
                    this.sourcetokenlist.add(asourcetoken);
                    switch(yychar) {
                        case 257:
                        case 258:
                        case 259:
                            if (insqlpluscmd) {
                                asourcetoken.insqlpluscmd = true;
                            }
                            break label142;
                        case 260:
                            if (insqlpluscmd) {
                                insqlpluscmd = false;
                                isvalidplace = true;
                                if (continuesqlplusatnewline) {
                                    insqlpluscmd = true;
                                    isvalidplace = false;
                                    asourcetoken.insqlpluscmd = true;
                                }
                            }

                            if (waitingreturnforsemicolon) {
                                isvalidplace = true;
                            }

                            if (waitingreturnforfloatdiv) {
                                isvalidplace = true;
                                lct.tokencode = 273;
                                if (lct.tokentype != ETokenType.ttslash) {
                                    lct.tokentype = ETokenType.ttsqlpluscmd;
                                }
                            }

                            this.flexer.insqlpluscmd = insqlpluscmd;
                            break label142;
                    }

                    continuesqlplusatnewline = false;
                    waitingreturnforsemicolon = false;
                    waitingreturnforfloatdiv = false;
                    if (insqlpluscmd) {
                        asourcetoken.insqlpluscmd = true;
                        if (asourcetoken.astext.equalsIgnoreCase("-")) {
                            continuesqlplusatnewline = true;
                        }
                    } else {
                        if (asourcetoken.tokentype == ETokenType.ttsemicolon) {
                            waitingreturnforsemicolon = true;
                        }

                        if (asourcetoken.tokentype == ETokenType.ttslash && (isvalidplace || this.IsValidPlaceForDivToSqlplusCmd(this.sourcetokenlist, asourcetoken.posinlist))) {
                            lct = asourcetoken;
                            waitingreturnforfloatdiv = true;
                        }

                        if (isvalidplace && this.isvalidsqlpluscmdInPostgresql(asourcetoken.astext)) {
                            asourcetoken.tokencode = 273;
                            if (asourcetoken.tokentype != ETokenType.ttslash) {
                                asourcetoken.tokentype = ETokenType.ttsqlpluscmd;
                            }

                            insqlpluscmd = true;
                            this.flexer.insqlpluscmd = insqlpluscmd;
                        }
                    }

                    isvalidplace = false;
                    if (prevst != null) {
                        if (prevst.tokencode == 345 && asourcetoken.tokencode != this.flexer.getkeywordvalue("JOIN")) {
                            prevst.tokencode = 264;
                        }

                        if (prevst.tokencode == 321 && asourcetoken.tokencode == this.flexer.getkeywordvalue("DEFERRABLE")) {
                            prevst.tokencode = this.flexer.getkeywordvalue("NOT_DEFERRABLE");
                        }
                    }

                    if (asourcetoken.tokencode == 345) {
                        prevst = asourcetoken;
                    } else if (asourcetoken.tokencode == 321) {
                        prevst = asourcetoken;
                    } else {
                        prevst = null;
                    }

                    if (asourcetoken.tokencode == this.flexer.getkeywordvalue("DIRECT_LOAD") || asourcetoken.tokencode == this.flexer.getkeywordvalue("ALL")) {
                        TSourceToken lcprevst = this.getprevsolidtoken(asourcetoken);
                        if (lcprevst != null && lcprevst.tokencode == 306) {
                            lcprevst.tokencode = 285;
                        }
                    }

                    TSourceToken stPercent;
                    if (asourcetoken.tokencode == 534) {
                        stPercent = asourcetoken.searchToken(532, -2);
                        if (stPercent != null) {
                            stPercent.tokencode = 533;
                        }
                    }

                    if (asourcetoken.tokencode == 550) {
                        stPercent = asourcetoken.searchToken(37, -1);
                        if (stPercent != null) {
                            stPercent.tokencode = 296;
                        }
                    }
                }

                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken != null) {
                    yychar = asourcetoken.tokencode;
                } else {
                    yychar = 0;
                    if (waitingreturnforfloatdiv) {
                        lct.tokencode = 273;
                        if (lct.tokentype != ETokenType.ttslash) {
                            lct.tokentype = ETokenType.ttsqlpluscmd;
                        }
                    }
                }

                if (yychar == 0 && prevst != null && prevst.tokencode == 345) {
                    prevst.tokencode = 264;
                }
            }

        }
    }

    void donetezzatexttotokenlist() {
        boolean insqlpluscmd = false;
        boolean isvalidplace = true;
        boolean waitingreturnforfloatdiv = false;
        boolean waitingreturnforsemicolon = false;
        boolean continuesqlplusatnewline = false;
        TSourceToken lct = null;
        TSourceToken prevst = null;
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            int yychar = asourcetoken.tokencode;

            while(yychar > 0) {
                label136: {
                    this.sourcetokenlist.add(asourcetoken);
                    switch(yychar) {
                        case 257:
                        case 258:
                        case 259:
                            if (insqlpluscmd) {
                                asourcetoken.insqlpluscmd = true;
                            }
                            break label136;
                        case 260:
                            if (insqlpluscmd) {
                                insqlpluscmd = false;
                                isvalidplace = true;
                                if (continuesqlplusatnewline) {
                                    insqlpluscmd = true;
                                    isvalidplace = false;
                                    asourcetoken.insqlpluscmd = true;
                                }
                            }

                            if (waitingreturnforsemicolon) {
                                isvalidplace = true;
                            }

                            if (waitingreturnforfloatdiv) {
                                isvalidplace = true;
                                lct.tokencode = 273;
                                if (lct.tokentype != ETokenType.ttslash) {
                                    lct.tokentype = ETokenType.ttsqlpluscmd;
                                }
                            }

                            this.flexer.insqlpluscmd = insqlpluscmd;
                            break label136;
                    }

                    continuesqlplusatnewline = false;
                    waitingreturnforsemicolon = false;
                    waitingreturnforfloatdiv = false;
                    if (insqlpluscmd) {
                        asourcetoken.insqlpluscmd = true;
                        if (asourcetoken.astext.equalsIgnoreCase("-")) {
                            continuesqlplusatnewline = true;
                        }
                    } else {
                        if (asourcetoken.tokentype == ETokenType.ttsemicolon) {
                            waitingreturnforsemicolon = true;
                        }

                        if (asourcetoken.tokentype == ETokenType.ttslash && (isvalidplace || this.IsValidPlaceForDivToSqlplusCmd(this.sourcetokenlist, asourcetoken.posinlist))) {
                            lct = asourcetoken;
                            waitingreturnforfloatdiv = true;
                        }

                        if (isvalidplace && this.isvalidsqlpluscmdInPostgresql(asourcetoken.astext)) {
                            asourcetoken.tokencode = 273;
                            if (asourcetoken.tokentype != ETokenType.ttslash) {
                                asourcetoken.tokentype = ETokenType.ttsqlpluscmd;
                            }

                            insqlpluscmd = true;
                            this.flexer.insqlpluscmd = insqlpluscmd;
                        }
                    }

                    isvalidplace = false;
                    if (prevst != null) {
                        if (prevst.tokencode == 345 && asourcetoken.tokencode != this.flexer.getkeywordvalue("JOIN")) {
                            prevst.tokencode = 264;
                        }

                        if (prevst.tokencode == 321 && asourcetoken.tokencode == this.flexer.getkeywordvalue("DEFERRABLE")) {
                            prevst.tokencode = this.flexer.getkeywordvalue("NOT_DEFERRABLE");
                        }
                    }

                    if (asourcetoken.tokencode == 345) {
                        prevst = asourcetoken;
                    } else if (asourcetoken.tokencode == 321) {
                        prevst = asourcetoken;
                    } else {
                        prevst = null;
                    }

                    if (asourcetoken.tokencode == this.flexer.getkeywordvalue("DIRECT_LOAD") || asourcetoken.tokencode == this.flexer.getkeywordvalue("ALL")) {
                        TSourceToken lcprevst = this.getprevsolidtoken(asourcetoken);
                        if (lcprevst != null && lcprevst.tokencode == 306) {
                            lcprevst.tokencode = 285;
                        }
                    }

                    if (asourcetoken.tokencode == 534) {
                        TSourceToken stKeep = asourcetoken.searchToken(532, -2);
                        if (stKeep != null) {
                            stKeep.tokencode = 533;
                        }
                    }
                }

                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken != null) {
                    yychar = asourcetoken.tokencode;
                } else {
                    yychar = 0;
                    if (waitingreturnforfloatdiv) {
                        lct.tokencode = 273;
                        if (lct.tokentype != ETokenType.ttslash) {
                            lct.tokentype = ETokenType.ttsqlpluscmd;
                        }
                    }
                }

                if (yychar == 0 && prevst != null && prevst.tokencode == 345) {
                    prevst.tokencode = 264;
                }
            }

        }
    }

    private int countLines(String s) {
        int pos = 0;
        int lf = 0;
        int cr = 0;

        while(pos < s.length()) {
            if (s.charAt(pos) == '\r') {
                ++cr;
                ++pos;
            } else if (s.charAt(pos) == '\n') {
                ++lf;
                ++pos;
            } else {
                if (s.charAt(pos) != ' ') {
                    break;
                }

                ++pos;
            }
        }

        return lf >= cr ? lf : cr;
    }

    void dooraclesqltexttotokenlist() {
        boolean insqlpluscmd = false;
        boolean isvalidplace = true;
        boolean waitingreturnforfloatdiv = false;
        boolean waitingreturnforsemicolon = false;
        boolean continuesqlplusatnewline = false;
        ESqlPlusCmd currentCmdType = ESqlPlusCmd.spcUnknown;
        TSourceToken lct = null;
        TSourceToken prevst = null;
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            int yychar = asourcetoken.tokencode;

            while(yychar > 0) {
                TSourceToken stPrev;
                label292: {
                    this.sourcetokenlist.add(asourcetoken);
                    switch(yychar) {
                        case 257:
                        case 258:
                        case 259:
                            if (insqlpluscmd) {
                                asourcetoken.insqlpluscmd = true;
                            }
                            break label292;
                        case 260:
                            if (insqlpluscmd) {
                                insqlpluscmd = false;
                                isvalidplace = true;
                                if (continuesqlplusatnewline) {
                                    insqlpluscmd = true;
                                    isvalidplace = false;
                                    asourcetoken.insqlpluscmd = true;
                                }

                                if (!insqlpluscmd) {
                                    currentCmdType = ESqlPlusCmd.spcUnknown;
                                }
                            }

                            if (waitingreturnforsemicolon) {
                                isvalidplace = true;
                            }

                            if (waitingreturnforfloatdiv) {
                                isvalidplace = true;
                                lct.tokencode = 273;
                                if (lct.tokentype != ETokenType.ttslash) {
                                    lct.tokentype = ETokenType.ttsqlpluscmd;
                                }
                            }

                            if (this.countLines(asourcetoken.toString()) > 1) {
                                isvalidplace = true;
                            }

                            this.flexer.insqlpluscmd = insqlpluscmd;
                            break label292;
                    }

                    continuesqlplusatnewline = false;
                    waitingreturnforsemicolon = false;
                    waitingreturnforfloatdiv = false;
                    if (insqlpluscmd) {
                        asourcetoken.insqlpluscmd = true;
                        if (asourcetoken.astext.equalsIgnoreCase("-")) {
                            continuesqlplusatnewline = true;
                        }
                    } else {
                        if (asourcetoken.tokentype == ETokenType.ttsemicolon) {
                            waitingreturnforsemicolon = true;
                        }

                        if (asourcetoken.tokentype == ETokenType.ttslash && (isvalidplace || this.IsValidPlaceForDivToSqlplusCmd(this.sourcetokenlist, asourcetoken.posinlist))) {
                            lct = asourcetoken;
                            waitingreturnforfloatdiv = true;
                        }

                        currentCmdType = TSqlplusCmdStatement.searchCmd(asourcetoken.astext, asourcetoken.nextToken());
                        if (currentCmdType != ESqlPlusCmd.spcUnknown) {
                            if (isvalidplace) {
                                asourcetoken.prevTokenCode = asourcetoken.tokencode;
                                asourcetoken.tokencode = 273;
                                if (asourcetoken.tokentype != ETokenType.ttslash) {
                                    asourcetoken.tokentype = ETokenType.ttsqlpluscmd;
                                }

                                insqlpluscmd = true;
                                this.flexer.insqlpluscmd = insqlpluscmd;
                            } else if (asourcetoken.tokencode == 481 && this.sourcetokenlist.returnbeforecurtoken(true)) {
                                asourcetoken.tokencode = 273;
                                if (asourcetoken.tokentype != ETokenType.ttslash) {
                                    asourcetoken.tokentype = ETokenType.ttsqlpluscmd;
                                }

                                insqlpluscmd = true;
                                this.flexer.insqlpluscmd = insqlpluscmd;
                            } else if (this.sourcetokenlist.returnbeforecurtoken(true)) {
                                stPrev = this.sourcetokenlist.get(this.sourcetokenlist.curpos - 1);
                                if (this.countLines(stPrev.toString()) > 1) {
                                    asourcetoken.tokencode = 273;
                                    if (asourcetoken.tokentype != ETokenType.ttslash) {
                                        asourcetoken.tokentype = ETokenType.ttsqlpluscmd;
                                    }

                                    insqlpluscmd = true;
                                    this.flexer.insqlpluscmd = insqlpluscmd;
                                }
                            }
                        }
                    }

                    isvalidplace = false;
                    if (prevst != null) {
                        if (prevst.tokencode == 345) {
                            if (asourcetoken.tokencode != this.flexer.getkeywordvalue("JOIN")) {
                                prevst.tokencode = 264;
                            }
                        } else if (prevst.tokencode == 321 && asourcetoken.tokencode == this.flexer.getkeywordvalue("DEFERRABLE")) {
                            prevst.tokencode = this.flexer.getkeywordvalue("NOT_DEFERRABLE");
                        }
                    }

                    if (asourcetoken.tokencode == 345) {
                        prevst = asourcetoken;
                    } else if (asourcetoken.tokencode == 321) {
                        prevst = asourcetoken;
                    } else {
                        prevst = null;
                    }

                    TSourceToken lcprevst;
                    if (asourcetoken.tokencode != this.flexer.getkeywordvalue("DIRECT_LOAD") && asourcetoken.tokencode != this.flexer.getkeywordvalue("ALL")) {
                        if (asourcetoken.tokencode == 534) {
                            stPrev = asourcetoken.searchToken(532, -2);
                            if (stPrev != null) {
                                stPrev.tokencode = 533;
                            }
                        } else if (asourcetoken.tokencode == 346) {
                            stPrev = asourcetoken.searchToken(538, -1);
                            if (stPrev != null) {
                                asourcetoken.tokencode = 279;
                            }
                        } else if (asourcetoken.tokencode == 324) {
                            stPrev = asourcetoken.searchToken(346, -1);
                            if (stPrev != null) {
                                stPrev.tokencode = 279;
                            } else {
                                TSourceToken stNatural = asourcetoken.searchToken(539, -4);
                                if (stNatural != null) {
                                    stNatural.tokencode = 276;
                                }
                            }
                        } else if (asourcetoken.tokencode == 347) {
                            stPrev = asourcetoken.searchToken(346, -1);
                            if (stPrev != null) {
                                stPrev.tokencode = 279;
                            }
                        } else if (asourcetoken.tokencode == 525) {
                            stPrev = asourcetoken.searchToken(541, -2);
                            if (stPrev != null) {
                                stPrev.tokencode = 540;
                            }
                        } else if (asourcetoken.tokencode == 341) {
                            stPrev = asourcetoken.searchToken(541, -2);
                            if (stPrev != null) {
                                stPrev.tokencode = 540;
                            }
                        } else if (asourcetoken.tokencode == 543) {
                            stPrev = asourcetoken.searchToken(541, -2);
                            if (stPrev != null) {
                                stPrev.tokencode = 540;
                            }
                        } else if (asourcetoken.tokencode == 541) {
                            stPrev = asourcetoken.searchToken(314, -1);
                            if (stPrev != null) {
                                asourcetoken.tokencode = 540;
                            }

                            if (asourcetoken.tokencode == 541) {
                                stPrev = asourcetoken.searchToken(542, -1);
                                if (stPrev != null) {
                                    asourcetoken.tokencode = 540;
                                }
                            }

                            if (asourcetoken.tokencode == 541) {
                                stPrev = asourcetoken.searchToken(542, -1);
                                if (stPrev != null) {
                                    asourcetoken.tokencode = 540;
                                }
                            }

                            if (asourcetoken.tokencode == 541) {
                                stPrev = asourcetoken.searchToken(307, -1);
                                if (stPrev != null) {
                                    asourcetoken.tokencode = 540;
                                }
                            }

                            if (asourcetoken.tokencode == 541) {
                                stPrev = asourcetoken.searchToken(505, -1);
                                if (stPrev != null) {
                                    asourcetoken.tokencode = 540;
                                }
                            }

                            if (asourcetoken.tokencode == 541) {
                                stPrev = asourcetoken.searchToken(37, -1);
                                if (stPrev != null) {
                                    asourcetoken.tokencode = 540;
                                }
                            }
                        } else if (asourcetoken.tokencode != 340 && asourcetoken.tokencode != 512) {
                            if (asourcetoken.tokencode == 325) {
                                lcprevst = this.getprevsolidtoken(asourcetoken);
                                if (lcprevst != null && lcprevst.astext.equalsIgnoreCase("a")) {
                                    stPrev = this.getprevsolidtoken(lcprevst);
                                    if (stPrev != null && (stPrev.tokencode == 321 || stPrev.tokencode == 525)) {
                                        lcprevst.tokencode = 565;
                                        asourcetoken.tokencode = 566;
                                    }
                                }
                            }
                        } else {
                            lcprevst = this.getprevsolidtoken(asourcetoken);
                            if (lcprevst != null && lcprevst.tokencode == 273 && lcprevst.toString().equalsIgnoreCase("connect")) {
                                lcprevst.tokencode = 481;
                                lcprevst.tokentype = ETokenType.ttkeyword;
                                this.flexer.insqlpluscmd = false;
                                continuesqlplusatnewline = false;
                                waitingreturnforsemicolon = false;
                                waitingreturnforfloatdiv = false;
                                isvalidplace = false;
                                insqlpluscmd = false;
                            }
                        }
                    } else {
                        lcprevst = this.getprevsolidtoken(asourcetoken);
                        if (lcprevst != null && lcprevst.tokencode == 306) {
                            lcprevst.tokencode = 285;
                        }
                    }
                }

                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken != null) {
                    yychar = asourcetoken.tokencode;
                    if (asourcetoken.tokencode == 46 && this.getprevsolidtoken(asourcetoken) != null && (currentCmdType == ESqlPlusCmd.spcAppend || currentCmdType == ESqlPlusCmd.spcChange || currentCmdType == ESqlPlusCmd.spcInput || currentCmdType == ESqlPlusCmd.spcList || currentCmdType == ESqlPlusCmd.spcRun)) {
                        stPrev = this.getprevsolidtoken(asourcetoken);
                        stPrev.insqlpluscmd = false;
                        if (stPrev.prevTokenCode != 0) {
                            stPrev.tokencode = stPrev.prevTokenCode;
                        } else {
                            stPrev.tokencode = 264;
                        }

                        this.flexer.insqlpluscmd = false;
                        continuesqlplusatnewline = false;
                        waitingreturnforsemicolon = false;
                        waitingreturnforfloatdiv = false;
                        isvalidplace = false;
                        insqlpluscmd = false;
                    }
                } else {
                    yychar = 0;
                    if (waitingreturnforfloatdiv) {
                        lct.tokencode = 273;
                        if (lct.tokentype != ETokenType.ttslash) {
                            lct.tokentype = ETokenType.ttsqlpluscmd;
                        }
                    }
                }

                if (yychar == 0 && prevst != null && prevst.tokencode == 345) {
                    prevst.tokencode = 264;
                }
            }

        }
    }

    void dodb2sqltexttotokenlist() {
        boolean insqlpluscmd = false;
        boolean isvalidplace = true;
        int jdbc_escape_nest = 0;
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            for(int yychar = asourcetoken.tokencode; yychar > 0; yychar = asourcetoken.tokencode) {
                switch(yychar) {
                    case 258:
                        if (asourcetoken.astext.toLowerCase().indexOf("scriptoptions") >= 0) {
                            asourcetoken.tokencode = 281;
                            asourcetoken.tokentype = ETokenType.ttidentifier;
                        }

                        if (insqlpluscmd) {
                            asourcetoken.insqlpluscmd = true;
                        }
                        break;
                    case 260:
                        insqlpluscmd = false;
                        isvalidplace = true;
                        break;
                    case 541:
                        ++jdbc_escape_nest;
                        asourcetoken.tokencode = 259;
                        break;
                    case 542:
                        --jdbc_escape_nest;
                        asourcetoken.tokencode = 259;
                        break;
                    default:
                        if (asourcetoken.tokencode == 530 || asourcetoken.tokencode == 531 || asourcetoken.tokencode == 532 || asourcetoken.tokencode == 533) {
                            TSourceToken lcprevst = this.getprevsolidtoken(asourcetoken);
                            if (lcprevst != null && lcprevst.tokencode == 311) {
                                lcprevst.tokencode = 534;
                            }
                        }

                        if (insqlpluscmd) {
                            asourcetoken.insqlpluscmd = true;
                        } else if (isvalidplace && TBaseType.mycomparetext(asourcetoken.astext.toLowerCase(), "echo") == 0) {
                            asourcetoken.tokencode = 273;
                            if (asourcetoken.tokentype != ETokenType.ttslash) {
                                asourcetoken.tokentype = ETokenType.ttsqlpluscmd;
                            }

                            asourcetoken.insqlpluscmd = true;
                            insqlpluscmd = true;
                        }

                        isvalidplace = false;
                }

                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken == null) {
                    break;
                }
            }

        }
    }

    void doteradatatexttotokenlist() {
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            for(int yychar = asourcetoken.tokencode; yychar > 0; yychar = asourcetoken.tokencode) {
                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken == null) {
                    break;
                }

                TSourceToken lcprevst;
                if (asourcetoken.tokencode != 552 && asourcetoken.tokencode != 553) {
                    if (asourcetoken.tokencode == 59) {
                        lcprevst = this.getprevsolidtoken(asourcetoken);
                        if (lcprevst != null && lcprevst.tokencode == 59) {
                            asourcetoken.tokencode = 273;
                        }
                    } else if (asourcetoken.tokencode == 275 && asourcetoken.toString().toLowerCase().endsWith("begin")) {
                        asourcetoken.tokencode = 351;
                        asourcetoken.tokentype = ETokenType.ttkeyword;
                        lcprevst = this.getprevsolidtoken(asourcetoken);
                        if (lcprevst != null) {
                            lcprevst.tokencode = 276;
                        }
                    }
                } else {
                    lcprevst = this.getprevsolidtoken(asourcetoken);
                    if (lcprevst != null && lcprevst.tokencode == 321) {
                        lcprevst.tokencode = 285;
                    }
                }
            }

        }
    }

    void docommonsqltexttotokenlist() {
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            for(int yychar = asourcetoken.tokencode; yychar > 0; yychar = asourcetoken.tokencode) {
                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken == null) {
                    break;
                }
            }

        }
    }

    void doodbcsqltexttotokenlist() {
        boolean insideODBC = false;
        TSourceToken odbcPrefix = null;
        this.domssqlsqltexttotokenlist();

        for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
            TSourceToken ast = this.sourcetokenlist.get(i);
            if (ast.tokencode == 123 || ast.tokencode == 277) {
                insideODBC = true;
                odbcPrefix = ast;
            }

            if (ast.tokencode == 125 || ast.tokencode == 278) {
                insideODBC = false;
                odbcPrefix = null;
            }

            if ((ast.tokencode == 531 || ast.tokencode == 532 || ast.tokencode == 533 || ast.tokencode == 535 || ast.tokencode == 534) && !insideODBC) {
                ast.tokencode = 264;
            }

            if (ast.tokencode == 468 && insideODBC) {
                odbcPrefix.setLinkToken(ast);
            }
        }

    }

    void dodaxsqltexttotokenlist() {
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            for(int yychar = asourcetoken.tokencode; yychar > 0; yychar = asourcetoken.tokencode) {
                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken == null) {
                    break;
                }
            }

        }
    }

    void dohanasqltexttotokenlist() {
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            for(int yychar = asourcetoken.tokencode; yychar > 0; yychar = asourcetoken.tokencode) {
                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken == null) {
                    break;
                }
            }

        }
    }

    void dohivetexttotokenlist() {
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            for(int yychar = asourcetoken.tokencode; yychar > 0; yychar = asourcetoken.tokencode) {
                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken == null) {
                    break;
                }

                if (asourcetoken.tokencode == 534) {
                    TSourceToken token = asourcetoken.searchToken(41, -1);
                    if (token != null) {
                        asourcetoken.tokencode = 264;
                    }
                } else if (asourcetoken.tokencode == 40) {
                }
            }

        }
    }

    void doimpalatexttotokenlist() {
        this.dohivetexttotokenlist();
    }

    void checkMySQLCommentToken(TSourceToken cmtToken) {
    }

    void dosparksqltexttotokenlist() {
        boolean startDelimiter = false;
        this.flexer.tmpDelimiter = "";
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            for(int yychar = asourcetoken.tokencode; yychar > 0; yychar = asourcetoken.tokencode) {
                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken == null) {
                    break;
                }

                this.checkMySQLCommentToken(asourcetoken);
                if (asourcetoken.tokencode == 260 && startDelimiter) {
                    startDelimiter = false;
                    this.flexer.tmpDelimiter = this.sourcetokenlist.get(this.sourcetokenlist.size() - 1).astext;
                }

                if (asourcetoken.tokencode == 537) {
                    TSourceToken lcprevst = this.getprevsolidtoken(asourcetoken);
                    if (lcprevst != null && lcprevst.tokencode == 311) {
                        lcprevst.tokencode = 279;
                    }
                }
            }

        }
    }

    void doathenatexttotokenlist() {
        boolean startDelimiter = false;
        this.flexer.tmpDelimiter = "";
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            for(int yychar = asourcetoken.tokencode; yychar > 0; yychar = asourcetoken.tokencode) {
                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken == null) {
                    break;
                }

                this.checkMySQLCommentToken(asourcetoken);
                if (asourcetoken.tokencode == 260 && startDelimiter) {
                    startDelimiter = false;
                    this.flexer.tmpDelimiter = this.sourcetokenlist.get(this.sourcetokenlist.size() - 1).astext;
                }
            }

        }
    }

    void doprestotexttotokenlist() {
        boolean startDelimiter = false;
        this.flexer.tmpDelimiter = "";
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            for(int yychar = asourcetoken.tokencode; yychar > 0; yychar = asourcetoken.tokencode) {
                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken == null) {
                    break;
                }

                this.checkMySQLCommentToken(asourcetoken);
                if (asourcetoken.tokencode == 260 && startDelimiter) {
                    startDelimiter = false;
                    this.flexer.tmpDelimiter = this.sourcetokenlist.get(this.sourcetokenlist.size() - 1).astext;
                }
            }

        }
    }

    void domysqltexttotokenlist() {
        boolean startDelimiter = false;
        this.flexer.tmpDelimiter = "";
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            int yychar = asourcetoken.tokencode;
            this.checkMySQLCommentToken(asourcetoken);
            if (asourcetoken.tokencode == 536) {
                startDelimiter = true;
            }

            for(; yychar > 0; yychar = asourcetoken.tokencode) {
                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken == null) {
                    break;
                }

                this.checkMySQLCommentToken(asourcetoken);
                if (asourcetoken.tokencode == 260 && startDelimiter) {
                    startDelimiter = false;
                    this.flexer.tmpDelimiter = this.sourcetokenlist.get(this.sourcetokenlist.size() - 1).astext;
                }

                if (asourcetoken.tokencode == 536) {
                    startDelimiter = true;
                }

                TSourceToken lcprevst;
                if (asourcetoken.tokencode == 537) {
                    lcprevst = this.getprevsolidtoken(asourcetoken);
                    if (lcprevst != null && lcprevst.tokencode == 311) {
                        lcprevst.tokencode = 279;
                    }
                }

                if (asourcetoken.tokencode == 548 || asourcetoken.tokencode == 549 || asourcetoken.tokencode == 550) {
                    lcprevst = this.getprevsolidtoken(asourcetoken);
                    if (lcprevst != null && lcprevst.tokencode != 123) {
                        asourcetoken.tokencode = 264;
                    }
                }
            }

        }
    }

    void doMdxtexttotokenlist() {
        TSourceToken asourcetoken = this.getanewsourcetoken();
        if (asourcetoken != null) {
            for(int yychar = asourcetoken.tokencode; yychar > 0; yychar = asourcetoken.tokencode) {
                this.sourcetokenlist.add(asourcetoken);
                asourcetoken = this.getanewsourcetoken();
                if (asourcetoken == null) {
                    break;
                }
            }

        }
    }

    void dosqltexttotokenlist() {
        switch(this.dbVendor) {
            case dbvmssql:
            case dbvazuresql:
                this.domssqlsqltexttotokenlist();
                break;
            case dbvaccess:
            case dbvansi:
            case dbvgeneric:
            default:
                this.docommonsqltexttotokenlist();
                break;
            case dbvsybase:
                this.dosybasesqltexttotokenlist();
                break;
            case dbvinformix:
                this.doinformixtexttotokenlist();
                break;
            case dbvoracle:
                this.dooraclesqltexttotokenlist();
                break;
            case dbvdb2:
                this.dodb2sqltexttotokenlist();
                break;
            case dbvmysql:
                this.domysqltexttotokenlist();
                break;
            case dbvteradata:
                this.doteradatatexttotokenlist();
                break;
            case dbvpostgresql:
                this.dopostgresqltexttotokenlist();
                break;
            case dbvredshift:
                this.doredshifttexttotokenlist();
                break;
            case dbvgreenplum:
                this.dogreenplumtexttotokenlist();
                break;
            case dbvmdx:
                this.doMdxtexttotokenlist();
                break;
            case dbvnetezza:
                this.donetezzatexttotokenlist();
                break;
            case dbvhive:
                this.dohivetexttotokenlist();
                break;
            case dbvimpala:
                this.doimpalatexttotokenlist();
                break;
            case dbvhana:
                this.dohanasqltexttotokenlist();
                break;
            case dbvdax:
                this.dodaxsqltexttotokenlist();
                break;
            case dbvodbc:
                this.doodbcsqltexttotokenlist();
                break;
            case dbvvertica:
                this.doverticatexttotokenlist();
                break;
            case dbvopenedge:
                this.domssqlsqltexttotokenlist();
                break;
            case dbvcouchbase:
                this.docouchbasesqltexttotokenlist();
                break;
            case dbvsnowflake:
                this.dosnowflakesqltexttotokenlist();
                break;
            case dbvbigquery:
                this.dobigquerysqltexttotokenlist();
                break;
            case dbvsoql:
                this.dosoqlsqltexttotokenlist();
                break;
            case dbvsparksql:
                this.dosparksqltexttotokenlist();
                break;
            case dbvathena:
                this.doathenatexttotokenlist();
                break;
            case dbvpresto:
                this.doprestotexttotokenlist();
        }

        if (this.sourcetokenlist.size() > 0) {
            TSourceToken lcPrevToken = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                this.sourcetokenlist.get(i).setPrevTokenInChain(lcPrevToken);
                if (i != this.sourcetokenlist.size() - 1) {
                    this.sourcetokenlist.get(i).setNextTokenInChain(this.sourcetokenlist.get(i + 1));
                }

                lcPrevToken = this.sourcetokenlist.get(i);
            }
        }

        this.closeFileStream();
    }

    void doongetrawsqlstatementevent(TCustomSqlStatement pcsqlstatement) {
        pcsqlstatement.setGsqlparser(this);
        pcsqlstatement.parser = this.fparser;
        pcsqlstatement.plsqlparser = this.fplsqlparser;
        pcsqlstatement.setStartToken(pcsqlstatement.sourcetokenlist.get(0));
        pcsqlstatement.setEndToken(pcsqlstatement.sourcetokenlist.get(pcsqlstatement.sourcetokenlist.size() - 1));
        this.sqlstatements.add(pcsqlstatement);
    }

    int doredshiftgetrawsqlstatements() {
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                switch(gst) {
                    case sterror:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            if (ast.tokencode == 273) {
                                gst = EFindSqlStateType.stsqlplus;
                                this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else {
                                this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                                if (this.gcurrentsqlstatement != null) {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else {
                                    this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                    ast.tokentype = ETokenType.tttokenlizererrortoken;
                                    gst = EFindSqlStateType.sterror;
                                    this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                    this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            }
                        } else {
                            if (this.gcurrentsqlstatement != null) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }

                            if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                ast.tokentype = ETokenType.ttsimplecomment;
                                ast.tokencode = 258;
                            }
                        }
                        break;
                    case stsqlplus:
                        if (ast.insqlpluscmd) {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        } else {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }
                        break;
                    case stsql:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (this.sourcetokenlist.sqlplusaftercurtoken()) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                }
            }

            if (this.gcurrentsqlstatement != null && (gst == EFindSqlStateType.stsqlplus || gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return this.syntaxErrors.size();
        }
    }

    int doverticagetrawsqlstatements() {
        int waitingEnd = 0;
        boolean foundEnd = false;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                TSourceToken st1;
                if (ast.tokencode == 394) {
                    st1 = ast.nextSolidToken();
                    if (st1 != null && st1.tokencode == 40) {
                        ast.tokencode = 532;
                    }
                } else if (ast.tokencode == 533 || ast.tokencode == 534) {
                    st1 = ast.nextSolidToken();
                    if (st1 != null && st1.tokencode != 40) {
                        ast.tokencode = 264;
                    }
                }

                switch(gst) {
                    case sterror:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            if (ast.tokencode == 273) {
                                gst = EFindSqlStateType.stsqlplus;
                                this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else {
                                this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                                if (this.gcurrentsqlstatement != null) {
                                    if (this.gcurrentsqlstatement.isverticaplsql()) {
                                        gst = EFindSqlStateType.ststoredprocedure;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        foundEnd = true;
                                        if (ast.tokencode == 351 || ast.tokencode == 479 || ast.searchToken(479, 4) != null) {
                                            waitingEnd = 1;
                                        }
                                    } else {
                                        gst = EFindSqlStateType.stsql;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }
                                } else {
                                    this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                    ast.tokentype = ETokenType.tttokenlizererrortoken;
                                    gst = EFindSqlStateType.sterror;
                                    this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                    this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            }
                        } else {
                            if (this.gcurrentsqlstatement != null) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }

                            if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                ast.tokentype = ETokenType.ttsimplecomment;
                                ast.tokencode = 258;
                            }
                        }
                        break;
                    case stsqlplus:
                        if (ast.insqlpluscmd) {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        } else {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }
                        break;
                    case stsql:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (this.sourcetokenlist.sqlplusaftercurtoken()) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                        break;
                    case ststoredprocedure:
                        if (ast.tokencode == 351) {
                            ++waitingEnd;
                            foundEnd = false;
                        } else if (ast.tokencode == 305) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 316) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 490) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 313) {
                            foundEnd = true;
                            --waitingEnd;
                            if (waitingEnd < 0) {
                                waitingEnd = 0;
                            }
                        }

                        if (ast.tokentype == ETokenType.ttslash && ast.tokencode == 273) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (ast.tokentype == ETokenType.ttperiod && this.sourcetokenlist.returnaftercurtoken(false) && this.sourcetokenlist.returnbeforecurtoken(false)) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            if (ast.tokentype == ETokenType.ttsemicolon && waitingEnd == 0 && foundEnd && this.gcurrentsqlstatement.VerticaStatementCanBeSeparatedByBeginEndPair()) {
                                gst = EFindSqlStateType.stnormal;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            }
                        }

                        if (ast.tokencode == 273) {
                            int m = this.flexer.getkeywordvalue(ast.astext);
                            if (m != 0) {
                                ast.tokencode = m;
                            } else {
                                ast.tokencode = 264;
                            }
                        }
                }
            }

            if (this.gcurrentsqlstatement != null && (gst == EFindSqlStateType.stsqlplus || gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return this.syntaxErrors.size();
        }
    }

    int dobigquerygetrawsqlstatements() {
        int errorcount = 0;
        this.gcurrentsqlstatement = null;
        EFindSqlStateType gst = EFindSqlStateType.stnormal;
        int beginNested = 0;
        boolean waitingDelimiter = false;
        this.userDelimiterStr = this.defaultDelimiterStr;

        for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
            TSourceToken ast = this.sourcetokenlist.get(i);
            this.sourcetokenlist.curpos = i;
            TSourceToken nextToken;
            if (ast.tokencode == 529) {
                nextToken = ast.nextSolidToken();
                if (nextToken != null && nextToken.tokencode == 40) {
                    ast.tokencode = 530;
                }
            } else if (ast.tokencode == 395) {
                nextToken = ast.nextSolidToken();
                if (nextToken != null && nextToken.tokencode == 262) {
                    ast.tokencode = 531;
                }
            } else if (ast.tokencode == 393) {
                nextToken = ast.nextSolidToken();
                if (nextToken != null) {
                    if (nextToken.tokencode == 262) {
                        ast.tokencode = 533;
                    } else if (nextToken.tokencode == 40) {
                        ast.tokencode = 264;
                    }
                }
            } else if (ast.tokencode == 394) {
                nextToken = ast.nextSolidToken();
                if (nextToken != null && nextToken.tokencode == 262) {
                    ast.tokencode = 532;
                }
            }

            switch(gst) {
                case sterror:
                    if (ast.tokentype == ETokenType.ttsemicolon) {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        gst = EFindSqlStateType.stnormal;
                    } else {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                    }
                    break;
                case stnormal:
                    if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                        this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                        if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                            ESqlStatementType[] ses = new ESqlStatementType[]{ESqlStatementType.sstcreateprocedure, ESqlStatementType.sstcreatefunction, ESqlStatementType.sstcreatetrigger};
                            if (this.includesqlstatementtype(this.gcurrentsqlstatement.sqlstatementtype, ses)) {
                                gst = EFindSqlStateType.ststoredprocedure;
                                waitingDelimiter = false;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                                this.curdelimiterchar = ';';
                            } else {
                                gst = EFindSqlStateType.stsql;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        }

                        if (!TBaseType.assigned(this.gcurrentsqlstatement)) {
                            this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                            ast.tokentype = ETokenType.tttokenlizererrortoken;
                            gst = EFindSqlStateType.sterror;
                            this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                            this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                    } else if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                    }
                    break;
                case stsqlplus:
                    if (ast.tokencode == 260) {
                        gst = EFindSqlStateType.stnormal;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    } else {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                    }
                    break;
                case stsql:
                    if (ast.tokentype == ETokenType.ttsemicolon && this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmysqldelimiter) {
                        gst = EFindSqlStateType.stnormal;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.gcurrentsqlstatement.semicolonended = ast;
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    } else if (ast.toString().equalsIgnoreCase(this.userDelimiterStr)) {
                        gst = EFindSqlStateType.stnormal;
                        ast.tokencode = 59;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.gcurrentsqlstatement.semicolonended = ast;
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    } else {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        if (ast.tokencode == 260 && this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmysqldelimiter) {
                            gst = EFindSqlStateType.stnormal;
                            this.userDelimiterStr = "";

                            for(int k = 0; k < this.gcurrentsqlstatement.sourcetokenlist.size(); ++k) {
                                TSourceToken st = this.gcurrentsqlstatement.sourcetokenlist.get(k);
                                if (st.tokencode != 536 && st.tokencode != 260 && st.tokencode != 259) {
                                    this.userDelimiterStr = this.userDelimiterStr + st.toString();
                                }
                            }

                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }
                    }
                    break;
                case ststoredprocedure:
                    if (waitingDelimiter) {
                        if (this.userDelimiterStr.equalsIgnoreCase(ast.toString())) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            continue;
                        }

                        if (this.userDelimiterStr.startsWith(ast.toString())) {
                            String lcstr = ast.toString();

                            int k;
                            TSourceToken st;
                            for(k = ast.posinlist + 1; k < ast.container.size(); ++k) {
                                st = ast.container.get(k);
                                if (st.tokencode == 536 || st.tokencode == 260 || st.tokencode == 259) {
                                    break;
                                }

                                lcstr = lcstr + st.toString();
                            }

                            if (this.userDelimiterStr.equalsIgnoreCase(lcstr)) {
                                for(k = ast.posinlist; k < ast.container.size(); ++k) {
                                    st = ast.container.get(k);
                                    if (st.tokencode == 536 || st.tokencode == 260 || st.tokencode == 259) {
                                        break;
                                    }

                                    ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                                }

                                gst = EFindSqlStateType.stnormal;
                                this.gcurrentsqlstatement.semicolonended = ast;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                continue;
                            }
                        }
                    }

                    this.gcurrentsqlstatement.addtokentolist(ast);
                    if (ast.tokencode == 351) {
                        ++beginNested;
                    } else if (ast.tokencode == 316) {
                        ++beginNested;
                    }

                    if (ast.tokencode == 313) {
                        nextToken = ast.nextSolidToken();
                        if (nextToken == null || nextToken.tokencode != 460 && nextToken.tokencode != 490 && nextToken.tokencode != 305) {
                            --beginNested;
                            if (beginNested == 0) {
                                waitingDelimiter = true;
                            }
                        }
                    }

                    if (this.userDelimiterStr.equals(";") && waitingDelimiter && ast.tokentype == ETokenType.ttsemicolon) {
                        gst = EFindSqlStateType.stnormal;
                        this.gcurrentsqlstatement.semicolonended = ast;
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    }
            }
        }

        if (TBaseType.assigned(this.gcurrentsqlstatement) && (gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
        }

        return errorcount;
    }

    int docouchbasegetrawsqlstatements() {
        int waitingEnd = 0;
        boolean foundEnd = false;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                switch(gst) {
                    case sterror:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            if (ast.tokencode == 273) {
                                gst = EFindSqlStateType.stsqlplus;
                                this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else {
                                this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                                if (this.gcurrentsqlstatement != null) {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else {
                                    this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                    ast.tokentype = ETokenType.tttokenlizererrortoken;
                                    gst = EFindSqlStateType.sterror;
                                    this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                    this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            }
                        } else {
                            if (this.gcurrentsqlstatement != null) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }

                            if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                ast.tokentype = ETokenType.ttsimplecomment;
                                ast.tokencode = 258;
                            }
                        }
                        break;
                    case stsqlplus:
                        if (ast.insqlpluscmd) {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        } else {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }
                        break;
                    case stsql:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (this.sourcetokenlist.sqlplusaftercurtoken()) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                        break;
                    case ststoredprocedure:
                        if (ast.tokencode == 351) {
                            ++waitingEnd;
                            foundEnd = false;
                        } else if (ast.tokencode == 305) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 316) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 490) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 313) {
                            foundEnd = true;
                            --waitingEnd;
                            if (waitingEnd < 0) {
                                waitingEnd = 0;
                            }
                        }

                        if (ast.tokentype == ETokenType.ttslash && ast.tokencode == 273) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (ast.tokentype == ETokenType.ttperiod && this.sourcetokenlist.returnaftercurtoken(false) && this.sourcetokenlist.returnbeforecurtoken(false)) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            if (ast.tokentype == ETokenType.ttsemicolon && waitingEnd == 0 && foundEnd && this.gcurrentsqlstatement.VerticaStatementCanBeSeparatedByBeginEndPair()) {
                                gst = EFindSqlStateType.stnormal;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            }
                        }

                        if (ast.tokencode == 273) {
                            int m = this.flexer.getkeywordvalue(ast.astext);
                            if (m != 0) {
                                ast.tokencode = m;
                            } else {
                                ast.tokencode = 264;
                            }
                        }
                }
            }

            if (this.gcurrentsqlstatement != null && (gst == EFindSqlStateType.stsqlplus || gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return this.syntaxErrors.size();
        }
    }

    int doathenagetrawsqlstatements() {
        int waitingEnd = 0;
        boolean foundEnd = false;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                switch(gst) {
                    case sterror:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                            if (this.gcurrentsqlstatement != null) {
                                if (this.gcurrentsqlstatement.ispgplsql()) {
                                    gst = EFindSqlStateType.ststoredprocedure;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    foundEnd = false;
                                    if (ast.tokencode == 351 || ast.tokencode == 479 || ast.searchToken(479, 4) != null) {
                                        waitingEnd = 1;
                                    }
                                } else {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            } else {
                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        } else {
                            if (this.gcurrentsqlstatement != null) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }

                            if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                ast.tokentype = ETokenType.ttsimplecomment;
                                ast.tokencode = 258;
                            }
                        }
                    case stsqlplus:
                    default:
                        break;
                    case stsql:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (this.sourcetokenlist.sqlplusaftercurtoken()) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                        break;
                    case ststoredprocedure:
                        if (ast.tokencode == 351) {
                            ++waitingEnd;
                        } else if (ast.tokencode == 305) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 316) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 490) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 313) {
                            foundEnd = true;
                            --waitingEnd;
                            if (waitingEnd < 0) {
                                waitingEnd = 0;
                            }
                        }

                        if (ast.tokentype == ETokenType.ttslash && ast.tokencode == 273) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (ast.tokentype == ETokenType.ttperiod && this.sourcetokenlist.returnaftercurtoken(false) && this.sourcetokenlist.returnbeforecurtoken(false)) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            if (ast.tokentype == ETokenType.ttsemicolon && waitingEnd == 0 && foundEnd) {
                                gst = EFindSqlStateType.stnormal;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            }
                        }

                        if (ast.tokencode == 273) {
                            int m = this.flexer.getkeywordvalue(ast.astext);
                            if (m != 0) {
                                ast.tokencode = m;
                            } else {
                                ast.tokencode = 264;
                            }
                        }
                }
            }

            if (this.gcurrentsqlstatement != null && (gst == EFindSqlStateType.stsqlplus || gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return this.syntaxErrors.size();
        }
    }

    int doprestogetrawsqlstatements() {
        int waitingEnd = 0;
        boolean foundEnd = false;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                switch(gst) {
                    case sterror:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                            if (this.gcurrentsqlstatement != null) {
                                if (this.gcurrentsqlstatement.ispgplsql()) {
                                    gst = EFindSqlStateType.ststoredprocedure;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    foundEnd = false;
                                    if (ast.tokencode == 351 || ast.tokencode == 479 || ast.searchToken(479, 4) != null) {
                                        waitingEnd = 1;
                                    }
                                } else {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            } else {
                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        } else {
                            if (this.gcurrentsqlstatement != null) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }

                            if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                ast.tokentype = ETokenType.ttsimplecomment;
                                ast.tokencode = 258;
                            }
                        }
                    case stsqlplus:
                    default:
                        break;
                    case stsql:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (this.sourcetokenlist.sqlplusaftercurtoken()) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                        break;
                    case ststoredprocedure:
                        if (ast.tokencode == 351) {
                            ++waitingEnd;
                        } else if (ast.tokencode == 305) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 316) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 490) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 313) {
                            foundEnd = true;
                            --waitingEnd;
                            if (waitingEnd < 0) {
                                waitingEnd = 0;
                            }
                        }

                        if (ast.tokentype == ETokenType.ttslash && ast.tokencode == 273) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (ast.tokentype == ETokenType.ttperiod && this.sourcetokenlist.returnaftercurtoken(false) && this.sourcetokenlist.returnbeforecurtoken(false)) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            if (ast.tokentype == ETokenType.ttsemicolon && waitingEnd == 0 && foundEnd) {
                                gst = EFindSqlStateType.stnormal;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            }
                        }

                        if (ast.tokencode == 273) {
                            int m = this.flexer.getkeywordvalue(ast.astext);
                            if (m != 0) {
                                ast.tokencode = m;
                            } else {
                                ast.tokencode = 264;
                            }
                        }
                }
            }

            if (this.gcurrentsqlstatement != null && (gst == EFindSqlStateType.stsqlplus || gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return this.syntaxErrors.size();
        }
    }

    int dopostgresqlgetrawsqlstatements() {
        int waitingEnd = 0;
        boolean foundEnd = false;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                TSourceToken st1;
                if (ast.tokencode == 298) {
                    st1 = ast.searchToken(262, 1);
                    if (st1 == null) {
                        ast.tokencode = 264;
                    }
                } else if (ast.tokencode == 553) {
                    st1 = ast.nextSolidToken();
                    if (st1 != null && st1.tokencode == 40) {
                        ast.tokencode = 552;
                    }
                }

                switch(gst) {
                    case sterror:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            if (ast.tokencode == 273) {
                                gst = EFindSqlStateType.stsqlplus;
                                this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else {
                                this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                                if (this.gcurrentsqlstatement != null) {
                                    if (this.gcurrentsqlstatement.ispgplsql()) {
                                        gst = EFindSqlStateType.ststoredprocedure;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        foundEnd = false;
                                        if (ast.tokencode == 351 || ast.tokencode == 479 || ast.searchToken(479, 4) != null) {
                                            waitingEnd = 1;
                                        }
                                        continue;
                                    }

                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    continue;
                                }

                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        } else {
                            if (this.gcurrentsqlstatement != null) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }

                            if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                ast.tokentype = ETokenType.ttsimplecomment;
                                ast.tokencode = 258;
                            }
                        }
                        break;
                    case stsqlplus:
                        if (ast.insqlpluscmd) {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        } else {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }
                        break;
                    case stsql:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (this.sourcetokenlist.sqlplusaftercurtoken()) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                        break;
                    case ststoredprocedure:
                        if (ast.tokencode == 351) {
                            ++waitingEnd;
                        } else if (ast.tokencode == 305) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 316) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 490) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 313) {
                            foundEnd = true;
                            --waitingEnd;
                            if (waitingEnd < 0) {
                                waitingEnd = 0;
                            }
                        }

                        if (ast.tokentype == ETokenType.ttslash && ast.tokencode == 273) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (ast.tokentype == ETokenType.ttperiod && this.sourcetokenlist.returnaftercurtoken(false) && this.sourcetokenlist.returnbeforecurtoken(false)) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            if (ast.tokentype == ETokenType.ttsemicolon && waitingEnd == 0 && foundEnd) {
                                gst = EFindSqlStateType.stnormal;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            }
                        }

                        if (ast.tokencode == 273) {
                            int m = this.flexer.getkeywordvalue(ast.astext);
                            if (m != 0) {
                                ast.tokencode = m;
                            } else {
                                ast.tokencode = 264;
                            }
                        }
                }
            }

            if (this.gcurrentsqlstatement != null && (gst == EFindSqlStateType.stsqlplus || gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return this.syntaxErrors.size();
        }
    }

    int dosnowflakegetrawsqlstatements() {
        int waitingEnd = 0;
        boolean foundEnd = false;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                TSourceToken stLparen;
                if (ast.tokencode != 344 && ast.tokencode != 343) {
                    if (ast.tokencode == 538) {
                        stLparen = ast.searchToken(40, 1);
                        if (stLparen != null) {
                            ast.tokencode = 539;
                        }
                    } else if (ast.tokencode == 394) {
                        stLparen = ast.searchToken(40, 1);
                        if (stLparen != null) {
                            ast.tokencode = 541;
                        } else {
                            stLparen = ast.searchToken(46, 1);
                            if (stLparen != null) {
                                ast.tokencode = 264;
                            }
                        }
                    } else if (ast.tokencode == 393) {
                        stLparen = ast.searchToken(40, 1);
                        if (stLparen != null) {
                            ast.tokencode = 542;
                        } else {
                            stLparen = ast.searchToken(46, 1);
                            if (stLparen != null) {
                                ast.tokencode = 264;
                            }
                        }
                    } else if (ast.tokencode == 379) {
                        stLparen = ast.searchToken(40, 1);
                        if (stLparen != null) {
                            ast.tokencode = 543;
                        } else {
                            stLparen = ast.searchToken(46, 1);
                            if (stLparen != null) {
                                ast.tokencode = 264;
                            }
                        }
                    }
                } else {
                    stLparen = ast.searchToken(40, 1);
                    if (stLparen != null) {
                        ast.tokencode = 264;
                    }
                }

                switch(gst) {
                    case sterror:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            if (ast.tokencode == 273) {
                                gst = EFindSqlStateType.stsqlplus;
                                this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else {
                                this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                                if (this.gcurrentsqlstatement != null) {
                                    if (this.gcurrentsqlstatement.ispgplsql()) {
                                        gst = EFindSqlStateType.ststoredprocedure;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        foundEnd = false;
                                        if (ast.tokencode == 351 || ast.tokencode == 479 || ast.searchToken(479, 4) != null) {
                                            waitingEnd = 1;
                                        }
                                    } else {
                                        gst = EFindSqlStateType.stsql;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }
                                } else {
                                    this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                    ast.tokentype = ETokenType.tttokenlizererrortoken;
                                    gst = EFindSqlStateType.sterror;
                                    this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                    this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            }
                        } else {
                            if (this.gcurrentsqlstatement != null) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }

                            if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                ast.tokentype = ETokenType.ttsimplecomment;
                                ast.tokencode = 258;
                            }
                        }
                        break;
                    case stsqlplus:
                        if (ast.insqlpluscmd) {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        } else {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }
                        break;
                    case stsql:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (this.sourcetokenlist.sqlplusaftercurtoken()) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                        break;
                    case ststoredprocedure:
                        if (ast.tokencode == 351) {
                            ++waitingEnd;
                        } else if (ast.tokencode == 305) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 316) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 490) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 313) {
                            foundEnd = true;
                            --waitingEnd;
                            if (waitingEnd < 0) {
                                waitingEnd = 0;
                            }
                        }

                        if (ast.tokentype == ETokenType.ttslash && ast.tokencode == 273) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (ast.tokentype == ETokenType.ttperiod && this.sourcetokenlist.returnaftercurtoken(false) && this.sourcetokenlist.returnbeforecurtoken(false)) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            if (ast.tokentype == ETokenType.ttsemicolon && waitingEnd == 0 && foundEnd && this.gcurrentsqlstatement.OracleStatementCanBeSeparatedByBeginEndPair()) {
                                gst = EFindSqlStateType.stnormal;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            }
                        }

                        if (ast.tokencode == 273) {
                            int m = this.flexer.getkeywordvalue(ast.astext);
                            if (m != 0) {
                                ast.tokencode = m;
                            } else {
                                ast.tokencode = 264;
                            }
                        }
                }
            }

            if (this.gcurrentsqlstatement != null && (gst == EFindSqlStateType.stsqlplus || gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return this.syntaxErrors.size();
        }
    }

    int dogreenplumgetrawsqlstatements() {
        int waitingEnd = 0;
        boolean foundEnd = false;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                TSourceToken nextst;
                if (ast.tokencode == 394) {
                    nextst = ast.nextSolidToken();
                    if (nextst != null && nextst.tokencode == 40) {
                        ast.tokencode = 552;
                    }
                } else if (ast.tokencode == 554) {
                    nextst = ast.nextSolidToken();
                    if (nextst != null && nextst.tokencode == 40) {
                        ast.tokencode = 553;
                    }
                }

                switch(gst) {
                    case sterror:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            if (ast.tokencode == 92) {
                                gst = EFindSqlStateType.stsqlplus;
                                this.gcurrentsqlstatement = new TSlashCommand(this.dbVendor);
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else {
                                this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                                if (this.gcurrentsqlstatement != null) {
                                    if (this.gcurrentsqlstatement.ispgplsql()) {
                                        gst = EFindSqlStateType.ststoredprocedure;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        foundEnd = false;
                                        if (ast.tokencode == 351 || ast.tokencode == 479 || ast.searchToken(479, 4) != null) {
                                            waitingEnd = 1;
                                        }
                                        continue;
                                    }

                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    continue;
                                }

                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        } else {
                            if (this.gcurrentsqlstatement != null) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }

                            if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                ast.tokentype = ETokenType.ttsimplecomment;
                                ast.tokencode = 258;
                            }
                        }
                        break;
                    case stsqlplus:
                        if (ast.tokencode == 260) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }

                        if (ast.tokencode == 92) {
                            nextst = ast.searchToken(92, 1);
                            if (nextst != null) {
                                gst = EFindSqlStateType.stnormal;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            } else {
                                gst = EFindSqlStateType.stsqlplus;
                                this.gcurrentsqlstatement = new TSlashCommand(this.dbVendor);
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                        break;
                    case stsql:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (this.sourcetokenlist.sqlplusaftercurtoken()) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                        break;
                    case ststoredprocedure:
                        if (ast.tokencode == 351) {
                            ++waitingEnd;
                        } else if (ast.tokencode == 305) {
                            if (ast.searchToken(313, -1) == null && ast.searchToken(556, 1) == null && ast.searchToken(321, 1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 316) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 490) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 313) {
                            foundEnd = true;
                            --waitingEnd;
                            if (waitingEnd < 0) {
                                waitingEnd = 0;
                            }
                        }

                        if (ast.tokentype == ETokenType.ttslash && ast.tokencode == 273) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (ast.tokentype == ETokenType.ttperiod && this.sourcetokenlist.returnaftercurtoken(false) && this.sourcetokenlist.returnbeforecurtoken(false)) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (ast.tokencode == 315 && waitingEnd == 0) {
                            --i;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            if (ast.tokentype == ETokenType.ttsemicolon && waitingEnd == 0 && foundEnd && this.gcurrentsqlstatement.OracleStatementCanBeSeparatedByBeginEndPair()) {
                                gst = EFindSqlStateType.stnormal;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            }
                        }

                        if (ast.tokencode == 273) {
                            int m = this.flexer.getkeywordvalue(ast.astext);
                            if (m != 0) {
                                ast.tokencode = m;
                            } else {
                                ast.tokencode = 264;
                            }
                        }
                }
            }

            if (this.gcurrentsqlstatement != null && (gst == EFindSqlStateType.stsqlplus || gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return this.syntaxErrors.size();
        }
    }

    private boolean isTypeCastToken(TSourceToken ast) {
        boolean istypecasetoken = false;
        TSourceToken st = ast.searchToken(40, 1);
        if (st != null) {
            TSourceToken nst = st.searchToken(263, 1);
            istypecasetoken = nst == null;
        }

        return istypecasetoken;
    }

    int donetezzagetrawsqlstatements() {
        int waitingEnd = 0;
        boolean foundEnd = false;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                if (ast.tokencode == 360) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 532;
                    }
                } else if (ast.tokencode == 361) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 533;
                    }
                } else if (ast.tokencode == 357) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 534;
                    }
                } else if (ast.tokencode == 367) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 535;
                    }
                } else if (ast.tokencode == 374) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 536;
                    }
                } else if (ast.tokencode == 368) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 537;
                    }
                } else if (ast.tokencode == 372) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 538;
                    }
                } else if (ast.tokencode == 355) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 540;
                    }
                } else if (ast.tokencode == 379) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 541;
                    }
                } else if (ast.tokencode == 380) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 542;
                    }
                } else if (ast.tokencode == 382) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 543;
                    }
                } else if (ast.tokencode == 381) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 544;
                    }
                } else if (ast.tokencode == 394) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 545;
                    }
                } else if (ast.tokencode == 393) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 546;
                    }
                } else if (ast.tokencode == 395) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 547;
                    }
                } else if (ast.tokencode == 416) {
                    if (this.isTypeCastToken(ast)) {
                        ast.tokencode = 548;
                    }
                } else if (ast.tokencode == 375 && this.isTypeCastToken(ast)) {
                    ast.tokencode = 549;
                }

                switch(gst) {
                    case sterror:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            if (ast.tokencode == 273) {
                                gst = EFindSqlStateType.stsqlplus;
                                this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else {
                                this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                                if (this.gcurrentsqlstatement != null) {
                                    if (this.gcurrentsqlstatement.isnzplsql()) {
                                        gst = EFindSqlStateType.ststoredprocedure;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        foundEnd = false;
                                        if (ast.tokencode == 351 || ast.tokencode == 479 || ast.searchToken(479, 4) != null) {
                                            waitingEnd = 1;
                                        }
                                        continue;
                                    }

                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    continue;
                                }

                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        } else {
                            if (this.gcurrentsqlstatement != null) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }

                            if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                ast.tokentype = ETokenType.ttsimplecomment;
                                ast.tokencode = 258;
                            }
                        }
                        break;
                    case stsqlplus:
                        if (ast.insqlpluscmd) {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        } else {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }
                        break;
                    case stsql:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (this.sourcetokenlist.sqlplusaftercurtoken()) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                        break;
                    case ststoredprocedure:
                        if (ast.tokencode == 351) {
                            ++waitingEnd;
                        } else if (ast.tokencode == 305) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 316) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 490) {
                            if (ast.searchToken(313, -1) == null) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 313) {
                            foundEnd = true;
                            --waitingEnd;
                            if (waitingEnd < 0) {
                                waitingEnd = 0;
                            }
                        }

                        if (ast.tokentype == ETokenType.ttslash && ast.tokencode == 273) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (ast.tokentype == ETokenType.ttperiod && this.sourcetokenlist.returnaftercurtoken(false) && this.sourcetokenlist.returnbeforecurtoken(false)) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            if (ast.tokentype == ETokenType.ttsemicolon && waitingEnd == 0 && foundEnd && this.gcurrentsqlstatement.OracleStatementCanBeSeparatedByBeginEndPair()) {
                                gst = EFindSqlStateType.stnormal;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            }
                        }
                }
            }

            if (this.gcurrentsqlstatement != null && (gst == EFindSqlStateType.stsqlplus || gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return this.syntaxErrors.size();
        }
    }

    int dooraclegetrawsqlstatements() {
        int[] waitingEnds = new int[1024];
        TGSqlParser.stored_procedure_type[] sptype = new TGSqlParser.stored_procedure_type[1024];
        TGSqlParser.stored_procedure_status[] procedure_status = new TGSqlParser.stored_procedure_status[1024];
        boolean endBySlashOnly = true;
        int nestedProcedures = 0;
        int nestedParenthesis = 0;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                TSourceToken stNext;
                TSourceToken prevst;
                if (ast.tokencode == 427) {
                    stNext = ast.searchToken(317, 1);
                    if (stNext != null) {
                        ast.tokencode = 264;
                    }
                } else if (ast.tokencode == 548) {
                    stNext = ast.searchToken(340, -1);
                    if (stNext == null) {
                        ast.tokencode = 264;
                    } else {
                        ast.tokencode = 547;
                    }
                } else {
                    TSourceToken stPrev;
                    if (ast.tokencode == 549) {
                        stNext = ast.searchToken(41, -1);
                        if (stNext != null) {
                            ast.tokencode = 264;
                        }

                        stPrev = ast.searchToken(46, 1);
                        if (stPrev != null) {
                            ast.tokencode = 264;
                        }

                        prevst = ast.searchTokenAfterObjectName();
                        stPrev = ast.searchToken(46, 1);
                        if (stPrev == null && prevst != null && prevst.tokencode == 40) {
                            ast.tokencode = 558;
                        }
                    } else if (ast.tokencode == 550) {
                        stNext = ast.searchToken(40, 1);
                        if (stNext == null) {
                            ast.tokencode = 264;
                        }
                    } else if (ast.tokencode == 552) {
                        stNext = ast.searchToken(551, 1);
                        stPrev = ast.searchToken(310, -1);
                        if (stPrev == null) {
                            stPrev = ast.searchToken(559, -1);
                        }

                        if (stNext == null && stPrev == null) {
                            ast.tokencode = 264;
                        }
                    } else if (ast.tokencode == 303) {
                        stNext = ast.searchToken(46, -1);
                        if (stNext != null) {
                            ast.tokencode = 264;
                        }
                    } else if (ast.tokencode == 554) {
                        stNext = ast.searchToken(428, -1);
                        if (stNext != null) {
                            stNext.tokencode = 553;
                        }
                    } else if (ast.tokencode == 555) {
                        stNext = ast.searchToken(347, -1);
                        if (stNext != null) {
                            stNext.tokencode = 281;
                        }
                    } else if (ast.tokencode == 556) {
                        stNext = ast.searchToken("(", 2);
                        if (stNext != null) {
                            ast.tokencode = 557;
                        }
                    } else if (ast.tokencode == 513) {
                        stNext = ast.searchToken("key", 1);
                        if (stNext == null) {
                            ast.tokencode = 264;
                        }
                    } else if (ast.tokencode == 561) {
                        stNext = ast.searchToken(562, 2);
                        if (stNext == null) {
                            stNext = ast.searchToken(563, 2);
                        }

                        if (stNext != null) {
                            ast.tokencode = 564;
                        }
                    } else if (ast.tokencode == 531) {
                        stNext = ast.searchToken("(", 2);
                        if (stNext == null) {
                            ast.tokencode = 264;
                        }
                    } else if (ast.tokencode == 515) {
                        stNext = ast.nextSolidToken();
                        if (stNext == null) {
                            ast.tokencode = 264;
                        } else if (stNext.tokencode != 264) {
                            ast.tokencode = 264;
                        }
                    }
                }

                switch(gst) {
                    case sterror:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            if (ast.tokencode == 273) {
                                gst = EFindSqlStateType.stsqlplus;
                                this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else {
                                this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                                if (this.gcurrentsqlstatement != null) {
                                    if (this.gcurrentsqlstatement.isoracleplsql()) {
                                        nestedProcedures = 0;
                                        gst = EFindSqlStateType.ststoredprocedure;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                            case sstplsql_createprocedure:
                                                sptype[nestedProcedures] = TGSqlParser.stored_procedure_type.procedure;
                                                break;
                                            case sstplsql_createfunction:
                                                sptype[nestedProcedures] = TGSqlParser.stored_procedure_type.function;
                                                break;
                                            case sstplsql_createpackage:
                                                sptype[nestedProcedures] = TGSqlParser.stored_procedure_type.package_spec;
                                                if (ast.searchToken(526, 5) != null) {
                                                    sptype[nestedProcedures] = TGSqlParser.stored_procedure_type.package_body;
                                                }
                                                break;
                                            case sst_block_with_label:
                                                sptype[nestedProcedures] = TGSqlParser.stored_procedure_type.block_with_declare;
                                                if (ast.tokencode == 351) {
                                                    sptype[nestedProcedures] = TGSqlParser.stored_procedure_type.block_with_begin;
                                                }
                                                break;
                                            case sstplsql_createtrigger:
                                                sptype[nestedProcedures] = TGSqlParser.stored_procedure_type.create_trigger;
                                                break;
                                            case sstoraclecreatelibrary:
                                                sptype[nestedProcedures] = TGSqlParser.stored_procedure_type.create_library;
                                                break;
                                            case sstplsql_createtype_placeholder:
                                                gst = EFindSqlStateType.stsql;
                                                break;
                                            default:
                                                sptype[nestedProcedures] = TGSqlParser.stored_procedure_type.others;
                                        }

                                        if (sptype[0] == TGSqlParser.stored_procedure_type.block_with_declare) {
                                            endBySlashOnly = false;
                                            procedure_status[0] = TGSqlParser.stored_procedure_status.is_as;
                                        } else if (sptype[0] == TGSqlParser.stored_procedure_type.block_with_begin) {
                                            endBySlashOnly = false;
                                            procedure_status[0] = TGSqlParser.stored_procedure_status.body;
                                        } else if (sptype[0] == TGSqlParser.stored_procedure_type.procedure) {
                                            endBySlashOnly = false;
                                            procedure_status[0] = TGSqlParser.stored_procedure_status.start;
                                        } else if (sptype[0] == TGSqlParser.stored_procedure_type.function) {
                                            endBySlashOnly = false;
                                            procedure_status[0] = TGSqlParser.stored_procedure_status.start;
                                        } else if (sptype[0] == TGSqlParser.stored_procedure_type.package_spec) {
                                            endBySlashOnly = false;
                                            procedure_status[0] = TGSqlParser.stored_procedure_status.start;
                                        } else if (sptype[0] == TGSqlParser.stored_procedure_type.package_body) {
                                            endBySlashOnly = false;
                                            procedure_status[0] = TGSqlParser.stored_procedure_status.start;
                                        } else if (sptype[0] == TGSqlParser.stored_procedure_type.create_trigger) {
                                            endBySlashOnly = false;
                                            procedure_status[0] = TGSqlParser.stored_procedure_status.start;
                                        } else if (sptype[0] == TGSqlParser.stored_procedure_type.create_library) {
                                            endBySlashOnly = false;
                                            procedure_status[0] = TGSqlParser.stored_procedure_status.bodyend;
                                        } else {
                                            endBySlashOnly = true;
                                            procedure_status[0] = TGSqlParser.stored_procedure_status.bodyend;
                                        }

                                        if (ast.tokencode == 351 || ast.tokencode == 479 || ast.searchToken(479, 4) != null) {
                                            waitingEnds[nestedProcedures] = 1;
                                        }
                                        continue;
                                    }

                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    nestedParenthesis = 0;
                                    continue;
                                }

                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        } else {
                            if (this.gcurrentsqlstatement != null) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }

                            if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                ast.tokentype = ETokenType.ttsimplecomment;
                                ast.tokencode = 258;
                            }
                        }
                        break;
                    case stsqlplus:
                        if (ast.insqlpluscmd) {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        } else {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }
                        break;
                    case stsql:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else if (this.sourcetokenlist.sqlplusaftercurtoken()) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            if (ast.tokencode == 40) {
                                ++nestedParenthesis;
                            }

                            if (ast.tokencode == 41) {
                                --nestedParenthesis;
                                if (nestedParenthesis < 0) {
                                    nestedParenthesis = 0;
                                }
                            }

                            Boolean findNewStmt = false;
                            TCustomSqlStatement lcStmt = null;
                            if (nestedParenthesis == 0 && this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstcreatetable) {
                                lcStmt = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                                if (lcStmt != null) {
                                    findNewStmt = true;
                                    if (lcStmt.sqlstatementtype == ESqlStatementType.sstselect) {
                                        prevst = ast.prevSolidToken();
                                        if (prevst.tokencode == 341 || prevst.tokencode == 40 || prevst.tokencode == 41) {
                                            findNewStmt = false;
                                        }
                                    }
                                }
                            }

                            if (findNewStmt) {
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                this.gcurrentsqlstatement = lcStmt;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        }
                        break;
                    case ststoredprocedure:
                        if (procedure_status[nestedProcedures] != TGSqlParser.stored_procedure_status.bodyend) {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }

                        int var10002;
                        switch(procedure_status[nestedProcedures]) {
                            case start:
                                if (ast.tokencode != 341 && ast.tokencode != 525) {
                                    if (ast.tokencode == 351) {
                                        if (sptype[nestedProcedures] == TGSqlParser.stored_procedure_type.create_trigger) {
                                            var10002 = waitingEnds[nestedProcedures]++;
                                        }

                                        if (nestedProcedures > 0) {
                                            --nestedProcedures;
                                        }

                                        procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.body;
                                    } else if (ast.tokencode == 313) {
                                        if (nestedProcedures > 0 && waitingEnds[nestedProcedures - 1] == 1 && (sptype[nestedProcedures - 1] == TGSqlParser.stored_procedure_type.package_body || sptype[nestedProcedures - 1] == TGSqlParser.stored_procedure_type.package_spec)) {
                                            --nestedProcedures;
                                            procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.bodyend;
                                        }
                                    } else if (ast.tokencode != 477 && ast.tokencode != 478) {
                                        if (sptype[nestedProcedures] == TGSqlParser.stored_procedure_type.create_trigger && ast.tokencode == 315) {
                                            procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.is_as;
                                        } else if (sptype[nestedProcedures] == TGSqlParser.stored_procedure_type.create_trigger && ast.tokentype == ETokenType.ttslash && ast.tokencode == 273) {
                                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                                            gst = EFindSqlStateType.stnormal;
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        } else if (sptype[nestedProcedures] == TGSqlParser.stored_procedure_type.create_trigger) {
                                            if (ast.tokencode == 545) {
                                                stNext = ast.searchToken(560, -1);
                                                if (stNext != null) {
                                                    procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.body;
                                                    var10002 = waitingEnds[nestedProcedures]++;
                                                }
                                            }
                                        } else if (sptype[nestedProcedures] == TGSqlParser.stored_procedure_type.function && ast.tokencode == 528 && (ast.searchToken("aggregate", -1) != null || ast.searchToken("pipelined", -1) != null)) {
                                            if (nestedProcedures == 0) {
                                                gst = EFindSqlStateType.stsql;
                                            } else {
                                                procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.body;
                                                --nestedProcedures;
                                            }
                                        }
                                    } else if (nestedProcedures > 0 && waitingEnds[nestedProcedures] == 0 && procedure_status[nestedProcedures - 1] == TGSqlParser.stored_procedure_status.is_as) {
                                        --nestedProcedures;
                                        ++nestedProcedures;
                                        waitingEnds[nestedProcedures] = 0;
                                        procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.start;
                                    }
                                } else if (sptype[nestedProcedures] != TGSqlParser.stored_procedure_type.create_trigger && (sptype[0] != TGSqlParser.stored_procedure_type.package_spec || nestedProcedures <= 0)) {
                                    procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.is_as;
                                    if (ast.searchToken("language", 1) != null) {
                                        if (nestedProcedures == 0) {
                                            gst = EFindSqlStateType.stsql;
                                        } else {
                                            procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.body;
                                            --nestedProcedures;
                                        }
                                    }
                                }
                                break;
                            case is_as:
                                if (ast.tokencode != 477 && ast.tokencode != 478) {
                                    if (ast.tokencode == 351) {
                                        if (nestedProcedures != 0 || sptype[nestedProcedures] != TGSqlParser.stored_procedure_type.package_body && sptype[nestedProcedures] != TGSqlParser.stored_procedure_type.package_spec) {
                                            var10002 = waitingEnds[nestedProcedures]++;
                                        }

                                        procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.body;
                                    } else if (ast.tokencode == 313) {
                                        if (nestedProcedures != 0 || waitingEnds[nestedProcedures] != 1 || sptype[nestedProcedures] != TGSqlParser.stored_procedure_type.package_body && sptype[nestedProcedures] != TGSqlParser.stored_procedure_type.package_spec) {
                                            var10002 = waitingEnds[nestedProcedures]--;
                                        } else {
                                            procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.bodyend;
                                            var10002 = waitingEnds[nestedProcedures]--;
                                        }
                                    } else if (ast.tokencode == 316 && ast.searchToken(59, 1) == null) {
                                        var10002 = waitingEnds[nestedProcedures]++;
                                    }
                                } else {
                                    ++nestedProcedures;
                                    waitingEnds[nestedProcedures] = 0;
                                    procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.start;
                                    if (nestedProcedures > 1023) {
                                        gst = EFindSqlStateType.sterror;
                                        --nestedProcedures;
                                    }
                                }
                                break;
                            case body:
                                if (ast.tokencode == 351) {
                                    var10002 = waitingEnds[nestedProcedures]++;
                                } else if (ast.tokencode == 305) {
                                    if (ast.searchToken(59, 2) == null) {
                                        var10002 = waitingEnds[nestedProcedures]++;
                                    }
                                } else if (ast.tokencode == 316) {
                                    if (ast.searchToken(59, 2) == null && ast.searchToken(313, -1) == null) {
                                        var10002 = waitingEnds[nestedProcedures]++;
                                    }
                                } else if (ast.tokencode == 490) {
                                    if (ast.searchToken(313, -1) == null || ast.searchToken(59, 2) == null) {
                                        var10002 = waitingEnds[nestedProcedures]++;
                                    }
                                } else if (ast.tokencode == 313) {
                                    var10002 = waitingEnds[nestedProcedures]--;
                                    if (waitingEnds[nestedProcedures] == 0) {
                                        if (nestedProcedures == 0) {
                                            procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.bodyend;
                                        } else {
                                            --nestedProcedures;
                                            procedure_status[nestedProcedures] = TGSqlParser.stored_procedure_status.is_as;
                                        }
                                    }
                                } else if (waitingEnds[nestedProcedures] == 0 && ast.tokentype == ETokenType.ttslash && ast.tokencode == 273) {
                                    ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                }
                                break;
                            case bodyend:
                                if (ast.tokentype == ETokenType.ttslash && ast.tokencode == 273) {
                                    ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                } else if (ast.tokentype == ETokenType.ttperiod && this.sourcetokenlist.returnaftercurtoken(false) && this.sourcetokenlist.returnbeforecurtoken(false)) {
                                    ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                } else if (ast.searchToken(479, 1) != null && !endBySlashOnly) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                } else if (ast.searchToken(477, 1) != null && !endBySlashOnly) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                } else if (ast.searchToken(478, 1) != null && !endBySlashOnly) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                } else if (ast.searchToken(307, 1) != null && ast.searchToken(479, 4) != null && !endBySlashOnly) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                } else if (ast.searchToken(307, 1) != null && ast.searchToken(546, 4) != null && !endBySlashOnly) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                } else if (ast.searchToken(426, 1) != null && ast.searchToken(545, 2) != null && !endBySlashOnly) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                } else if (ast.searchToken(301, 1) != null && !endBySlashOnly) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                } else if (ast.searchToken(419, 1) != null && !endBySlashOnly) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                } else if (ast.searchToken(423, 1) != null && ast.searchToken(440, 2) != null && !endBySlashOnly) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                } else {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            case end:
                        }

                        if (ast.tokencode == 273) {
                            int m = this.flexer.getkeywordvalue(ast.astext);
                            if (m != 0) {
                                ast.tokencode = m;
                            } else if (ast.tokentype == ETokenType.ttslash) {
                                ast.tokencode = 47;
                            } else {
                                ast.tokencode = 264;
                            }
                        }

                        // int wrapped_keyword_max_pos = true;
                        if (ast.tokencode == 535 && ast.posinlist - this.gcurrentsqlstatement.sourcetokenlist.get(0).posinlist < 20) {
                            if (this.gcurrentsqlstatement instanceof TCommonStoredProcedureSqlStatement) {
                                ((TCommonStoredProcedureSqlStatement)this.gcurrentsqlstatement).setWrapped(true);
                            }

                            if (this.gcurrentsqlstatement instanceof TPlsqlCreatePackage && ast.prevSolidToken() != null) {
                                ((TPlsqlCreatePackage)this.gcurrentsqlstatement).setPackageName(this.fparser.getNf().createObjectNameWithPart(ast.prevSolidToken()));
                            }
                        }
                }
            }

            if (this.gcurrentsqlstatement != null && (gst == EFindSqlStateType.stsqlplus || gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return this.syntaxErrors.size();
        }
    }

    int domdxgetrawsqlstatements() {
        int waitingEnd = 0;
        boolean foundEnd = false;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                switch(gst) {
                    case sterror:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                            if (this.gcurrentsqlstatement != null) {
                                if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmdxscope) {
                                    gst = EFindSqlStateType.ststoredprocedure;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    foundEnd = false;
                                    waitingEnd = 1;
                                } else {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            } else {
                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        } else {
                            if (this.gcurrentsqlstatement != null) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }

                            if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                ast.tokentype = ETokenType.ttsimplecomment;
                                ast.tokencode = 258;
                            }
                        }
                    case stsqlplus:
                    default:
                        break;
                    case stsql:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                        break;
                    case ststoredprocedure:
                        if (ast.tokencode == 305) {
                            ++waitingEnd;
                        } else if (ast.tokencode == 316) {
                            ++waitingEnd;
                        } else if (ast.tokencode == 524) {
                            if (lcprevsolidtoken.tokencode != 313) {
                                ++waitingEnd;
                            }
                        } else if (ast.tokencode == 313) {
                            foundEnd = true;
                            --waitingEnd;
                            if (waitingEnd < 0) {
                                waitingEnd = 0;
                            }
                        }

                        this.gcurrentsqlstatement.addtokentolist(ast);
                        if (ast.tokentype == ETokenType.ttsemicolon && waitingEnd == 0 && foundEnd) {
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }
                }
            }

            if (this.gcurrentsqlstatement != null && (gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return this.syntaxErrors.size();
        }
    }

    int doimpalagetrawsqlstatements() {
        return this.dohivegetrawsqlstatements();
    }

    int dohivegetrawsqlstatements() {
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                if (ast.tokencode == 270) {
                    TSourceToken st1 = ast.searchToken(276, 1);
                    if (st1 == null) {
                        ast.tokencode = 264;
                    }
                }

                switch(gst) {
                    case sterror:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                            if (this.gcurrentsqlstatement != null) {
                                gst = EFindSqlStateType.stsql;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else {
                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        } else {
                            if (this.gcurrentsqlstatement != null) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }

                            if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                ast.tokentype = ETokenType.ttsimplecomment;
                                ast.tokencode = 258;
                            }
                        }
                    case stsqlplus:
                    default:
                        break;
                    case stsql:
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                }
            }

            if (this.gcurrentsqlstatement != null && (gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.sterror)) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return this.syntaxErrors.size();
        }
    }

    int dosybasegetrawsqlstatements() {
        int errorcount = 0;
        int case_end_nest = 0;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            int lcblocklevel = 0;
            int lctrycatchlevel = 0;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                if (ast.tokenstatus == ETokenStatus.tsignoredbygetrawstatement) {
                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                } else if (gst == EFindSqlStateType.ststoredprocedurebody && ast.tokencode != 463 && ast.tokencode != 307 && ast.tokencode != 426) {
                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                } else {
                    TCustomSqlStatement lcnextsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                    TSourceToken lcnextsolidtoken;
                    switch(gst) {
                        case sterror:
                            if (TBaseType.assigned(lcnextsqlstatement)) {
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                this.gcurrentsqlstatement = lcnextsqlstatement;
                                this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                gst = EFindSqlStateType.stsql;
                            } else if (ast.tokentype == ETokenType.ttsemicolon) {
                                this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                gst = EFindSqlStateType.stnormal;
                            } else {
                                this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            }
                            break;
                        case stnormal:
                            if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                                this.gcurrentsqlstatement = lcnextsqlstatement;
                                if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                    switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                        case sstmssqlcreateprocedure:
                                        case sstmssqlcreatefunction:
                                        case sstcreatetrigger:
                                        case sstmssqlalterprocedure:
                                        case sstmssqlalterfunction:
                                        case sstmssqlaltertrigger:
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            gst = EFindSqlStateType.ststoredprocedure;
                                            break;
                                        case sstmssqlbegintry:
                                        case sstmssqlbegincatch:
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            gst = EFindSqlStateType.sttrycatch;
                                            lctrycatchlevel = 0;
                                            break;
                                        case sstmssqlgo:
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            gst = EFindSqlStateType.stnormal;
                                            break;
                                        default:
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqllabel) {
                                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                                gst = EFindSqlStateType.stnormal;
                                            } else {
                                                gst = EFindSqlStateType.stsql;
                                            }
                                    }
                                } else if (ast.tokencode == 351) {
                                    this.gcurrentsqlstatement = new TMssqlBlock(this.dbVendor);
                                    this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstmssqlblock;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    gst = EFindSqlStateType.stblock;
                                } else if (this.sqlstatements.size() == 0) {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement = new TMssqlExecute(this.dbVendor);
                                    ast.tokencode = 566;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else if (this.sqlstatements.get(this.sqlstatements.size() - 1).sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement = new TMssqlExecute(this.dbVendor);
                                    ast.tokencode = 566;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }

                                if (!TBaseType.assigned(this.gcurrentsqlstatement)) {
                                    this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                    ast.tokentype = ETokenType.tttokenlizererrortoken;
                                    gst = EFindSqlStateType.sterror;
                                    this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                    this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            } else {
                                if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }

                                if (TBaseType.assigned(lcprevsolidtoken) && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                    ast.tokentype = ETokenType.ttsimplecomment;
                                    ast.tokencode = 258;
                                }
                            }
                        case stsqlplus:
                        default:
                            break;
                        case stsql:
                            if (ast.tokentype == ETokenType.ttsemicolon) {
                                boolean lcstillinsql = false;
                                if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif) {
                                    lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                    if (TBaseType.assigned(lcnextsolidtoken) && lcnextsolidtoken.tokencode == 349) {
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        lcstillinsql = true;
                                    }
                                }

                                if (!lcstillinsql) {
                                    gst = EFindSqlStateType.stnormal;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.gcurrentsqlstatement.semicolonended = ast;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                }
                            } else if (TBaseType.assigned(lcnextsqlstatement)) {
                                if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                        case sstmssqlif:
                                        case sstmssqlwhile:
                                            if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegincatch || lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegintry) {
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.stblock;
                                                lcblocklevel = 1;
                                                lcnextsqlstatement = null;
                                                continue;
                                            }

                                            if (this.gcurrentsqlstatement.dummytag == 1) {
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                if (lcnextsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlif && lcnextsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlwhile) {
                                                    this.gcurrentsqlstatement.dummytag = 0;
                                                } else {
                                                    this.gcurrentsqlstatement.dummytag = 1;
                                                }

                                                lcnextsqlstatement = null;
                                                continue;
                                            }
                                            break;
                                        case sstmssqlalterqueue:
                                            if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlexec) {
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                lcnextsqlstatement = null;
                                                continue;
                                            }
                                            break;
                                        case sstmssqlcreateschema:
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            lcnextsqlstatement = null;
                                            continue;
                                    }

                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                        case sstmssqlcreateprocedure:
                                        case sstmssqlcreatefunction:
                                        case sstcreatetrigger:
                                        case sstmssqlalterprocedure:
                                        case sstmssqlalterfunction:
                                        case sstmssqlaltertrigger:
                                            gst = EFindSqlStateType.ststoredprocedure;
                                            break;
                                        case sstmssqlbegintry:
                                        case sstmssqlbegincatch:
                                            gst = EFindSqlStateType.sttrycatch;
                                            lctrycatchlevel = 0;
                                            break;
                                        case sstmssqlgo:
                                            gst = EFindSqlStateType.stnormal;
                                            break;
                                        default:
                                            gst = EFindSqlStateType.stsql;
                                    }
                                }
                            } else if (ast.tokencode == 351) {
                                if (this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlif && this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlwhile) {
                                    if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqldeclare) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = new TMssqlBlock(this.dbVendor);
                                        this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstmssqlblock;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        gst = EFindSqlStateType.stblock;
                                    } else {
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }
                                    continue;
                                }

                                gst = EFindSqlStateType.stblock;
                                lcblocklevel = 0;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else if (ast.tokencode == 316) {
                                ++case_end_nest;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else if (ast.tokencode == 313) {
                                if (case_end_nest > 0) {
                                    --case_end_nest;
                                }

                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else if (ast.tokencode == 349) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                                if ((this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif || this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlwhile) && case_end_nest == 0) {
                                    this.gcurrentsqlstatement.dummytag = 1;
                                }
                            } else {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                                if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlset && ast.tokencode == 323) {
                                    gst = EFindSqlStateType.stnormal;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                }
                            }
                            break;
                        case ststoredprocedure:
                            if (TBaseType.assigned(lcnextsqlstatement)) {
                                if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    gst = EFindSqlStateType.ststoredprocedurebody;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    lcnextsqlstatement = null;
                                }
                            }

                            if (gst == EFindSqlStateType.ststoredprocedure) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                                if (ast.tokencode == 351) {
                                    gst = EFindSqlStateType.stblock;
                                }
                            }
                            break;
                        case stblock:
                            if (TBaseType.assigned(lcnextsqlstatement)) {
                                if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    lcnextsqlstatement = null;
                                }
                            }

                            if (gst == EFindSqlStateType.stblock) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                                if (ast.tokencode == 351) {
                                    lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                    if (TBaseType.assigned(lcnextsolidtoken)) {
                                        if (!TBaseType.mysametext(lcnextsolidtoken.astext, "tran") && !TBaseType.mysametext(lcnextsolidtoken.astext, "transaction") && !TBaseType.mysametext(lcnextsolidtoken.astext, "distributed") && !TBaseType.mysametext(lcnextsolidtoken.astext, "dialog") && !TBaseType.mysametext(lcnextsolidtoken.astext, "conversation")) {
                                            ++lcblocklevel;
                                        }
                                    } else {
                                        ++lcblocklevel;
                                    }
                                } else if (ast.tokencode == 316) {
                                    ++lcblocklevel;
                                } else if (ast.tokencode == 313) {
                                    boolean lcisendconversation = false;
                                    lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                    if (TBaseType.assigned(lcnextsolidtoken) && lcnextsolidtoken.tokencode == this.flexer.getkeywordvalue("conversation".toUpperCase())) {
                                        lcisendconversation = true;
                                    }

                                    if (!lcisendconversation) {
                                        if (lcblocklevel == 0) {
                                            if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif && TBaseType.assigned(lcnextsolidtoken)) {
                                                if (lcnextsolidtoken.tokencode == 349) {
                                                    gst = EFindSqlStateType.stsql;
                                                } else if (lcnextsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                                    TSourceToken lcnnextsolidtoken = this.sourcetokenlist.nextsolidtoken(lcnextsolidtoken.posinlist, 1, false);
                                                    if (TBaseType.assigned(lcnnextsolidtoken) && lcnnextsolidtoken.tokencode == 349) {
                                                        gst = EFindSqlStateType.stsql;
                                                    }
                                                }
                                            }

                                            if (gst != EFindSqlStateType.stsql) {
                                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                                gst = EFindSqlStateType.stnormal;
                                            }
                                        } else {
                                            --lcblocklevel;
                                        }
                                    }
                                }
                            }
                            break;
                        case sttrycatch:
                            if (TBaseType.assigned(lcnextsqlstatement)) {
                                if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegintry || lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegincatch) {
                                        ++lctrycatchlevel;
                                    }

                                    lcnextsqlstatement = null;
                                }
                            }

                            if (gst != EFindSqlStateType.sttrycatch) {
                                break;
                            }

                            this.gcurrentsqlstatement.addtokentolist(ast);
                            if ((ast.tokencode == 456 || ast.tokencode == 462) && TBaseType.assigned(lcprevsolidtoken) && lcprevsolidtoken.tokencode == 313) {
                                if (lctrycatchlevel == 0) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    --lctrycatchlevel;
                                }
                            }
                            break;
                        case ststoredprocedurebody:
                            if (TBaseType.assigned(lcnextsqlstatement)) {
                                switch(lcnextsqlstatement.sqlstatementtype) {
                                    case sstmssqlcreateprocedure:
                                    case sstmssqlcreatefunction:
                                    case sstcreatetrigger:
                                    case sstmssqlalterprocedure:
                                    case sstmssqlalterfunction:
                                    case sstmssqlaltertrigger:
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        gst = EFindSqlStateType.ststoredprocedure;
                                        break;
                                    case sstmssqlbegintry:
                                    case sstmssqlbegincatch:
                                    default:
                                        lcnextsqlstatement = null;
                                        break;
                                    case sstmssqlgo:
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                }
                            }

                            if (gst == EFindSqlStateType.ststoredprocedurebody) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                    }
                }
            }

            if (TBaseType.assigned(this.gcurrentsqlstatement) && gst != EFindSqlStateType.stnormal) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return errorcount;
        }
    }

    int doinformixgetrawsqlstatements() {
        int errorcount = 0;
        int case_end_nest = 0;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            int lcblocklevel = 0;
            int lctrycatchlevel = 0;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                if (ast.tokenstatus == ETokenStatus.tsignoredbygetrawstatement) {
                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                } else if (gst == EFindSqlStateType.ststoredprocedurebody && ast.tokencode != 463 && ast.tokencode != 307 && ast.tokencode != 426) {
                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                } else {
                    TCustomSqlStatement lcnextsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                    TSourceToken lcnextsolidtoken;
                    switch(gst) {
                        case sterror:
                            if (TBaseType.assigned(lcnextsqlstatement)) {
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                this.gcurrentsqlstatement = lcnextsqlstatement;
                                this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                gst = EFindSqlStateType.stsql;
                            } else if (ast.tokentype == ETokenType.ttsemicolon) {
                                this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                gst = EFindSqlStateType.stnormal;
                            } else {
                                this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            }
                            break;
                        case stnormal:
                            if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                                this.gcurrentsqlstatement = lcnextsqlstatement;
                                if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                    switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                        case sstcreatetrigger:
                                        case sstinformixCreateProcedure:
                                        case sstinformixCreateFunction:
                                        case sstinformixAlterProcedure:
                                        case sstinformixAlterFunction:
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            gst = EFindSqlStateType.ststoredprocedure;
                                            break;
                                        case sstmssqlalterprocedure:
                                        case sstmssqlalterfunction:
                                        case sstmssqlaltertrigger:
                                        case sstmssqlif:
                                        case sstmssqlwhile:
                                        case sstmssqlalterqueue:
                                        case sstmssqlcreateschema:
                                        default:
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            gst = EFindSqlStateType.stsql;
                                            break;
                                        case sstmssqlbegintry:
                                        case sstmssqlbegincatch:
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            gst = EFindSqlStateType.sttrycatch;
                                            lctrycatchlevel = 0;
                                            break;
                                        case sstmssqlgo:
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            gst = EFindSqlStateType.stnormal;
                                            break;
                                        case sstinformixExecute:
                                            gst = EFindSqlStateType.stExec;
                                    }
                                } else if (ast.tokencode == 351) {
                                    this.gcurrentsqlstatement = new TMssqlBlock(this.dbVendor);
                                    this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstmssqlblock;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    gst = EFindSqlStateType.stblock;
                                } else if (this.sqlstatements.size() == 0) {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement = new TMssqlExecute(this.dbVendor);
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else if (this.sqlstatements.get(this.sqlstatements.size() - 1).sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement = new TMssqlExecute(this.dbVendor);
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }

                                if (!TBaseType.assigned(this.gcurrentsqlstatement)) {
                                    this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                    ast.tokentype = ETokenType.tttokenlizererrortoken;
                                    gst = EFindSqlStateType.sterror;
                                    this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                    this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            } else {
                                if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }

                                if (TBaseType.assigned(lcprevsolidtoken) && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                    ast.tokentype = ETokenType.ttsimplecomment;
                                    ast.tokencode = 258;
                                }
                            }
                        case stsqlplus:
                        default:
                            break;
                        case stsql:
                            if (ast.tokentype == ETokenType.ttsemicolon) {
                                boolean lcstillinsql = false;
                                if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif) {
                                    lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                    if (TBaseType.assigned(lcnextsolidtoken) && lcnextsolidtoken.tokencode == 349) {
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        lcstillinsql = true;
                                    }
                                }

                                if (!lcstillinsql) {
                                    gst = EFindSqlStateType.stnormal;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.gcurrentsqlstatement.semicolonended = ast;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                }
                            } else if (TBaseType.assigned(lcnextsqlstatement)) {
                                if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                        case sstmssqlif:
                                        case sstmssqlwhile:
                                            if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegincatch || lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegintry) {
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.stblock;
                                                lcblocklevel = 1;
                                                lcnextsqlstatement = null;
                                                continue;
                                            }

                                            if (this.gcurrentsqlstatement.dummytag == 1) {
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                if (lcnextsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlif && lcnextsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlwhile) {
                                                    this.gcurrentsqlstatement.dummytag = 0;
                                                } else {
                                                    this.gcurrentsqlstatement.dummytag = 1;
                                                }

                                                lcnextsqlstatement = null;
                                                continue;
                                            }
                                            break;
                                        case sstmssqlalterqueue:
                                            if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlexec) {
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                lcnextsqlstatement = null;
                                                continue;
                                            }
                                            break;
                                        case sstmssqlcreateschema:
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            lcnextsqlstatement = null;
                                            continue;
                                    }

                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                        case sstcreatetrigger:
                                        case sstinformixCreateProcedure:
                                        case sstinformixCreateFunction:
                                        case sstinformixAlterProcedure:
                                        case sstinformixAlterFunction:
                                            gst = EFindSqlStateType.ststoredprocedure;
                                            break;
                                        case sstmssqlalterprocedure:
                                        case sstmssqlalterfunction:
                                        case sstmssqlaltertrigger:
                                        case sstmssqlif:
                                        case sstmssqlwhile:
                                        case sstmssqlalterqueue:
                                        case sstmssqlcreateschema:
                                        default:
                                            gst = EFindSqlStateType.stsql;
                                            break;
                                        case sstmssqlbegintry:
                                        case sstmssqlbegincatch:
                                            gst = EFindSqlStateType.sttrycatch;
                                            lctrycatchlevel = 0;
                                            break;
                                        case sstmssqlgo:
                                            gst = EFindSqlStateType.stnormal;
                                    }
                                }
                            } else if (ast.tokencode == 351) {
                                if (this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlif && this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlwhile) {
                                    if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqldeclare) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = new TMssqlBlock(this.dbVendor);
                                        this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstmssqlblock;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        gst = EFindSqlStateType.stblock;
                                    } else {
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }
                                    continue;
                                }

                                gst = EFindSqlStateType.stblock;
                                lcblocklevel = 0;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else if (ast.tokencode == 316) {
                                ++case_end_nest;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else if (ast.tokencode == 313) {
                                if (case_end_nest > 0) {
                                    --case_end_nest;
                                }

                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else {
                                if (ast.tokencode == 349) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    if ((this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif || this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlwhile) && case_end_nest == 0) {
                                        this.gcurrentsqlstatement.dummytag = 1;
                                    }
                                    continue;
                                }

                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                            break;
                        case ststoredprocedure:
                            if (TBaseType.assigned(lcnextsqlstatement)) {
                                if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    gst = EFindSqlStateType.ststoredprocedurebody;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    lcnextsqlstatement = null;
                                }
                            }

                            if (gst != EFindSqlStateType.ststoredprocedure) {
                                break;
                            }

                            this.gcurrentsqlstatement.addtokentolist(ast);
                            if ((ast.tokencode == 477 || ast.tokencode == 478) && ast.searchToken(313, -1) != null) {
                                gst = EFindSqlStateType.stsql;
                            }
                            break;
                        case stblock:
                            if (TBaseType.assigned(lcnextsqlstatement)) {
                                if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    lcnextsqlstatement = null;
                                }
                            }

                            if (gst == EFindSqlStateType.stblock) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                                if (ast.tokencode == 351) {
                                    lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                    if (TBaseType.assigned(lcnextsolidtoken)) {
                                        if (!TBaseType.mysametext(lcnextsolidtoken.astext, "tran") && !TBaseType.mysametext(lcnextsolidtoken.astext, "transaction") && !TBaseType.mysametext(lcnextsolidtoken.astext, "distributed") && !TBaseType.mysametext(lcnextsolidtoken.astext, "dialog") && !TBaseType.mysametext(lcnextsolidtoken.astext, "conversation")) {
                                            ++lcblocklevel;
                                        }
                                    } else {
                                        ++lcblocklevel;
                                    }
                                } else if (ast.tokencode == 316) {
                                    ++lcblocklevel;
                                } else if (ast.tokencode == 313) {
                                    boolean lcisendconversation = false;
                                    lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                    if (TBaseType.assigned(lcnextsolidtoken) && lcnextsolidtoken.tokencode == this.flexer.getkeywordvalue("conversation".toUpperCase())) {
                                        lcisendconversation = true;
                                    }

                                    if (!lcisendconversation) {
                                        if (lcblocklevel == 0) {
                                            if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif && TBaseType.assigned(lcnextsolidtoken)) {
                                                if (lcnextsolidtoken.tokencode == 349) {
                                                    gst = EFindSqlStateType.stsql;
                                                } else if (lcnextsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                                    TSourceToken lcnnextsolidtoken = this.sourcetokenlist.nextsolidtoken(lcnextsolidtoken.posinlist, 1, false);
                                                    if (TBaseType.assigned(lcnnextsolidtoken) && lcnnextsolidtoken.tokencode == 349) {
                                                        gst = EFindSqlStateType.stsql;
                                                    }
                                                }
                                            }

                                            if (gst != EFindSqlStateType.stsql) {
                                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                                gst = EFindSqlStateType.stnormal;
                                            }
                                        } else {
                                            --lcblocklevel;
                                        }
                                    }
                                }
                            }
                            break;
                        case sttrycatch:
                            if (TBaseType.assigned(lcnextsqlstatement)) {
                                if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegintry || lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegincatch) {
                                        ++lctrycatchlevel;
                                    }

                                    lcnextsqlstatement = null;
                                }
                            }

                            if (gst != EFindSqlStateType.sttrycatch) {
                                break;
                            }

                            this.gcurrentsqlstatement.addtokentolist(ast);
                            if ((ast.tokencode == 456 || ast.tokencode == 462) && TBaseType.assigned(lcprevsolidtoken) && lcprevsolidtoken.tokencode == 313) {
                                if (lctrycatchlevel == 0) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    --lctrycatchlevel;
                                }
                            }
                            break;
                        case ststoredprocedurebody:
                            if (TBaseType.assigned(lcnextsqlstatement)) {
                                switch(lcnextsqlstatement.sqlstatementtype) {
                                    case sstcreatetrigger:
                                    case sstinformixCreateProcedure:
                                    case sstinformixCreateFunction:
                                    case sstinformixAlterProcedure:
                                    case sstinformixAlterFunction:
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        gst = EFindSqlStateType.ststoredprocedure;
                                        break;
                                    case sstmssqlalterprocedure:
                                    case sstmssqlalterfunction:
                                    case sstmssqlaltertrigger:
                                    case sstmssqlbegintry:
                                    case sstmssqlbegincatch:
                                    case sstmssqlif:
                                    case sstmssqlwhile:
                                    case sstmssqlalterqueue:
                                    case sstmssqlcreateschema:
                                    default:
                                        lcnextsqlstatement = null;
                                        break;
                                    case sstmssqlgo:
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                }
                            }

                            if (gst == EFindSqlStateType.ststoredprocedurebody) {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                            break;
                        case stExec:
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            if (ast.tokentype == ETokenType.ttsemicolon) {
                                gst = EFindSqlStateType.stnormal;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            }
                    }
                }
            }

            if (TBaseType.assigned(this.gcurrentsqlstatement) && gst != EFindSqlStateType.stnormal) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return errorcount;
        }
    }

    int dosoqlgetrawsqlstatements() {
        int errorcount = 0;
        int case_end_nest = 0;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            int lcblocklevel = 0;
            int lctrycatchlevel = 0;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;
            int lcMergeInSelectNested = 0;
            boolean lcMergeInSelect = false;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                if (lcMergeInSelect) {
                    if (ast.tokencode == 40) {
                        ++lcMergeInSelectNested;
                    }

                    if (ast.tokencode == 41) {
                        --lcMergeInSelectNested;
                        if (lcMergeInSelectNested == 0) {
                            lcMergeInSelect = false;
                        }
                    }

                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                } else if (ast.tokenstatus == ETokenStatus.tsignoredbygetrawstatement) {
                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                } else {
                    TSourceToken st1;
                    if (ast.tokencode == 304) {
                        st1 = ast.nextSolidToken();
                        if (st1 != null && (st1.toString().equalsIgnoreCase("tracking") || st1.toString().equalsIgnoreCase("viewstat"))) {
                            ast.tokencode = 572;
                        }
                    }

                    if (this.dbVendor == EDbVendor.dbvopenedge) {
                        if (ast.tokencode == 333) {
                            st1 = ast.searchToken(340, 1);
                            if (st1 == null) {
                                ast.tokencode = 264;
                            }
                        } else if (ast.tokencode == 311) {
                            st1 = ast.searchToken(499, 1);
                            if (st1 != null) {
                                ast.tokencode = 529;
                            }
                        }
                    }

                    if (gst == EFindSqlStateType.ststoredprocedurebody && ast.tokencode != 463 && ast.tokencode != 307 && ast.tokencode != 426) {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                    } else {
                        TCustomSqlStatement lcnextsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                        TSourceToken lcnextsolidtoken;
                        switch(gst) {
                            case sterror:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                    gst = EFindSqlStateType.stsql;
                                } else if (ast.tokentype == ETokenType.ttsemicolon) {
                                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                }
                                break;
                            case stnormal:
                                if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                        switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                            case sstmssqlcreateprocedure:
                                            case sstmssqlcreatefunction:
                                            case sstcreatetrigger:
                                            case sstmssqlalterprocedure:
                                            case sstmssqlalterfunction:
                                            case sstmssqlaltertrigger:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.ststoredprocedure;
                                                break;
                                            case sstmssqlbegintry:
                                            case sstmssqlbegincatch:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.sttrycatch;
                                                lctrycatchlevel = 0;
                                                break;
                                            case sstmssqlgo:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                                gst = EFindSqlStateType.stnormal;
                                                break;
                                            default:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.stsql;
                                        }
                                    } else if (ast.tokencode == 351) {
                                        this.gcurrentsqlstatement = new TMssqlBlock(this.dbVendor);
                                        this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstmssqlblock;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        gst = EFindSqlStateType.stblock;
                                    } else if (this.sqlstatements.size() == 0) {
                                        gst = EFindSqlStateType.stsql;
                                        this.gcurrentsqlstatement = new TMssqlExecute(this.dbVendor);
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    } else if (this.sqlstatements.get(this.sqlstatements.size() - 1).sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        gst = EFindSqlStateType.stsql;
                                        this.gcurrentsqlstatement = new TMssqlExecute(this.dbVendor);
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }

                                    if (!TBaseType.assigned(this.gcurrentsqlstatement)) {
                                        this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                        ast.tokentype = ETokenType.tttokenlizererrortoken;
                                        gst = EFindSqlStateType.sterror;
                                        this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                        this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }
                                } else {
                                    if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }

                                    if (TBaseType.assigned(lcprevsolidtoken) && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                        ast.tokentype = ETokenType.ttsimplecomment;
                                        ast.tokencode = 258;
                                    }
                                }
                            case stsqlplus:
                            default:
                                break;
                            case stsql:
                                if (ast.tokentype == ETokenType.ttsemicolon) {
                                    boolean lcstillinsql = false;
                                    if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif) {
                                        lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                        if (TBaseType.assigned(lcnextsolidtoken) && lcnextsolidtoken.tokencode == 349) {
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            lcstillinsql = true;
                                        }
                                    }

                                    if (!lcstillinsql) {
                                        gst = EFindSqlStateType.stnormal;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.gcurrentsqlstatement.semicolonended = ast;
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    }
                                } else if (TBaseType.assigned(lcnextsqlstatement)) {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                    } else {
                                        switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                            case sstmssqlif:
                                            case sstmssqlwhile:
                                                if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegincatch || lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegintry) {
                                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                                    gst = EFindSqlStateType.stblock;
                                                    lcblocklevel = 1;
                                                    st1 = null;
                                                    continue;
                                                }

                                                if (this.gcurrentsqlstatement.dummytag == 1) {
                                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                                    if (lcnextsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlif && lcnextsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlwhile) {
                                                        this.gcurrentsqlstatement.dummytag = 0;
                                                    } else {
                                                        this.gcurrentsqlstatement.dummytag = 1;
                                                    }

                                                    st1 = null;
                                                    continue;
                                                }
                                                break;
                                            case sstmssqlalterqueue:
                                                if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlexec) {
                                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                                    st1 = null;
                                                    continue;
                                                }
                                                break;
                                            case sstmssqlcreateschema:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                st1 = null;
                                                continue;
                                        }

                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                            case sstmssqlcreateprocedure:
                                            case sstmssqlcreatefunction:
                                            case sstcreatetrigger:
                                            case sstmssqlalterprocedure:
                                            case sstmssqlalterfunction:
                                            case sstmssqlaltertrigger:
                                                gst = EFindSqlStateType.ststoredprocedure;
                                                break;
                                            case sstmssqlbegintry:
                                            case sstmssqlbegincatch:
                                                gst = EFindSqlStateType.sttrycatch;
                                                lctrycatchlevel = 0;
                                                break;
                                            case sstmssqlgo:
                                                gst = EFindSqlStateType.stnormal;
                                                break;
                                            default:
                                                gst = EFindSqlStateType.stsql;
                                        }
                                    }
                                } else if (ast.tokencode == 351) {
                                    if (this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlif && this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlwhile) {
                                        if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqldeclare) {
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            this.gcurrentsqlstatement = new TMssqlBlock(this.dbVendor);
                                            this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstmssqlblock;
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            gst = EFindSqlStateType.stblock;
                                        } else {
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                        }
                                        continue;
                                    }

                                    gst = EFindSqlStateType.stblock;
                                    lcblocklevel = 0;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else if (ast.tokencode == 316) {
                                    ++case_end_nest;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else if (ast.tokencode == 313) {
                                    if (case_end_nest > 0) {
                                        --case_end_nest;
                                    }

                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else {
                                    if (ast.tokencode == 349) {
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        if ((this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif || this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlwhile) && case_end_nest == 0) {
                                            this.gcurrentsqlstatement.dummytag = 1;
                                        }
                                        continue;
                                    }

                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                                break;
                            case ststoredprocedure:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                    } else {
                                        gst = EFindSqlStateType.ststoredprocedurebody;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        st1 = null;
                                    }
                                }

                                if (gst == EFindSqlStateType.ststoredprocedure) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    if (ast.tokencode == 351) {
                                        gst = EFindSqlStateType.stblock;
                                    }
                                }
                                break;
                            case stblock:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                    } else {
                                        st1 = null;
                                    }
                                }

                                if (gst == EFindSqlStateType.stblock) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    if (ast.tokencode == 351) {
                                        lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                        if (TBaseType.assigned(lcnextsolidtoken)) {
                                            if (!TBaseType.mysametext(lcnextsolidtoken.astext, "tran") && !TBaseType.mysametext(lcnextsolidtoken.astext, "transaction") && !TBaseType.mysametext(lcnextsolidtoken.astext, "distributed") && !TBaseType.mysametext(lcnextsolidtoken.astext, "dialog") && !TBaseType.mysametext(lcnextsolidtoken.astext, "conversation")) {
                                                ++lcblocklevel;
                                            }
                                        } else {
                                            ++lcblocklevel;
                                        }
                                    } else if (ast.tokencode == 316) {
                                        ++lcblocklevel;
                                    } else if (ast.tokencode == 313) {
                                        boolean lcisendconversation = false;
                                        lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                        if (TBaseType.assigned(lcnextsolidtoken) && lcnextsolidtoken.tokencode == this.flexer.getkeywordvalue("conversation".toUpperCase())) {
                                            lcisendconversation = true;
                                        }

                                        if (!lcisendconversation) {
                                            if (lcblocklevel == 0) {
                                                if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif && TBaseType.assigned(lcnextsolidtoken)) {
                                                    if (lcnextsolidtoken.tokencode == 349) {
                                                        gst = EFindSqlStateType.stsql;
                                                    } else if (lcnextsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                                        TSourceToken lcnnextsolidtoken = this.sourcetokenlist.nextsolidtoken(lcnextsolidtoken.posinlist, 1, false);
                                                        if (TBaseType.assigned(lcnnextsolidtoken) && lcnnextsolidtoken.tokencode == 349) {
                                                            gst = EFindSqlStateType.stsql;
                                                        }
                                                    }
                                                }

                                                if (gst != EFindSqlStateType.stsql) {
                                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                                    gst = EFindSqlStateType.stnormal;
                                                }
                                            } else {
                                                --lcblocklevel;
                                            }
                                        }
                                    }
                                }
                                break;
                            case sttrycatch:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                    } else {
                                        if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegintry || lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegincatch) {
                                            ++lctrycatchlevel;
                                        }

                                        st1 = null;
                                    }
                                }

                                if (gst != EFindSqlStateType.sttrycatch) {
                                    break;
                                }

                                this.gcurrentsqlstatement.addtokentolist(ast);
                                if ((ast.tokencode == 456 || ast.tokencode == 462) && TBaseType.assigned(lcprevsolidtoken) && lcprevsolidtoken.tokencode == 313) {
                                    if (lctrycatchlevel == 0) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                    } else {
                                        --lctrycatchlevel;
                                    }
                                }
                                break;
                            case ststoredprocedurebody:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    switch(lcnextsqlstatement.sqlstatementtype) {
                                        case sstmssqlcreateprocedure:
                                        case sstmssqlcreatefunction:
                                        case sstcreatetrigger:
                                        case sstmssqlalterprocedure:
                                        case sstmssqlalterfunction:
                                        case sstmssqlaltertrigger:
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            this.gcurrentsqlstatement = lcnextsqlstatement;
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            gst = EFindSqlStateType.ststoredprocedure;
                                            break;
                                        case sstmssqlbegintry:
                                        case sstmssqlbegincatch:
                                        case sstmssqlif:
                                        case sstmssqlwhile:
                                        case sstmssqlalterqueue:
                                        case sstmssqlcreateschema:
                                        case sstinformixCreateProcedure:
                                        case sstinformixCreateFunction:
                                        case sstinformixAlterProcedure:
                                        case sstinformixAlterFunction:
                                        case sstinformixExecute:
                                        default:
                                            st1 = null;
                                            break;
                                        case sstmssqlgo:
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            this.gcurrentsqlstatement = lcnextsqlstatement;
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            gst = EFindSqlStateType.stnormal;
                                            break;
                                        case sstcreateview:
                                        case sstcreatetable:
                                            boolean readForNewStmt = false;
                                            TSourceToken st1_1 = ast.searchToken(59, -1);
                                            if (st1_1 != null) {
                                                TSourceToken st2 = ast.searchToken(313, -2);
                                                if (st2 != null) {
                                                    readForNewStmt = true;
                                                }
                                            }

                                            if (readForNewStmt) {
                                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                                this.gcurrentsqlstatement = lcnextsqlstatement;
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.stsql;
                                            } else {
                                                st1_1 = null;
                                            }
                                    }
                                }

                                if (gst == EFindSqlStateType.ststoredprocedurebody) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                        }
                    }
                }
            }

            if (TBaseType.assigned(this.gcurrentsqlstatement) && gst != EFindSqlStateType.stnormal) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return errorcount;
        }
    }

    int domssqlgetrawsqlstatements() {
        int errorcount = 0;
        int case_end_nest = 0;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            int lcblocklevel = 0;
            int lctrycatchlevel = 0;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;
            int lcMergeInSelectNested = 0;
            boolean lcMergeInSelect = false;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                if (lcMergeInSelect) {
                    if (ast.tokencode == 40) {
                        ++lcMergeInSelectNested;
                    }

                    if (ast.tokencode == 41) {
                        --lcMergeInSelectNested;
                        if (lcMergeInSelectNested == 0) {
                            lcMergeInSelect = false;
                        }
                    }

                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                } else if (ast.tokenstatus == ETokenStatus.tsignoredbygetrawstatement) {
                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                } else {
                    TSourceToken prev2;
                    if (ast.tokencode == 353) {
                        prev2 = ast.searchToken(40, 1);
                        if (prev2 == null) {
                            prev2 = ast.searchToken(301, 1);
                            if (prev2 == null) {
                                ast.tokencode = 264;
                            }
                        }
                    } else if (ast.tokencode == 418) {
                        prev2 = ast.nextSolidToken();
                        if (prev2.tokencode == 324) {
                            ast.tokencode = 287;
                        }

                        if (lcprevsolidtoken != null && lcprevsolidtoken.tokencode == 40) {
                            lcMergeInSelect = true;
                            ++lcMergeInSelectNested;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            continue;
                        }
                    } else if (ast.tokencode == 558) {
                        if (lcprevsolidtoken != null && lcprevsolidtoken.tokencode == 46) {
                            prev2 = ast.searchToken(40, 1);
                            if (prev2 != null) {
                                ast.tokencode = 553;
                            }
                        }
                    } else if (ast.tokencode == 559) {
                        if (lcprevsolidtoken != null && lcprevsolidtoken.tokencode == 46) {
                            prev2 = ast.searchToken(40, 1);
                            if (prev2 != null) {
                                ast.tokencode = 554;
                            }
                        }
                    } else if (ast.tokencode == 560) {
                        if (lcprevsolidtoken != null && lcprevsolidtoken.tokencode == 46) {
                            prev2 = ast.searchToken(40, 1);
                            if (prev2 != null) {
                                ast.tokencode = 555;
                            }
                        }
                    } else if (ast.tokencode == 561) {
                        if (lcprevsolidtoken != null && lcprevsolidtoken.tokencode == 46) {
                            prev2 = ast.searchToken(40, 1);
                            if (prev2 != null) {
                                ast.tokencode = 556;
                            }
                        }
                    } else if (ast.tokencode == 562) {
                        if (lcprevsolidtoken != null && lcprevsolidtoken.tokencode == 46) {
                            prev2 = ast.searchToken(40, 1);
                            if (prev2 != null) {
                                ast.tokencode = 557;
                            }
                        }
                    } else if (ast.tokencode == 306) {
                        prev2 = ast.nextSolidToken();
                        if (prev2 != null && prev2.tokencode == 564) {
                            ast.tokencode = 285;
                        }
                    } else if (ast.tokencode == 566) {
                        prev2 = ast.nextSolidToken();
                        if (prev2 != null && prev2.tokencode == 46) {
                            ast.tokencode = 264;
                        }
                    } else if (ast.tokencode == 334 && lcprevsolidtoken != null && (lcprevsolidtoken.tokencode == 567 || lcprevsolidtoken.tokencode == 568)) {
                        prev2 = lcprevsolidtoken.searchToken(443, -1);
                        if (prev2 == null) {
                            ast.tokencode = 569;
                        }
                    }

                    if (this.dbVendor == EDbVendor.dbvopenedge) {
                        if (ast.tokencode == 333) {
                            prev2 = ast.searchToken(340, 1);
                            if (prev2 == null) {
                                ast.tokencode = 264;
                            }
                        } else if (ast.tokencode == 311) {
                            prev2 = ast.searchToken(499, 1);
                            if (prev2 != null) {
                                ast.tokencode = 529;
                            }
                        }
                    }

                    if (gst == EFindSqlStateType.ststoredprocedurebody && ast.tokencode != 463 && ast.tokencode != 307 && ast.tokencode != 426) {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                    } else {
                        TCustomSqlStatement lcnextsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                        TSourceToken lcnextsolidtoken;
                        switch(gst) {
                            case sterror:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                    gst = EFindSqlStateType.stsql;
                                } else if (ast.tokentype == ETokenType.ttsemicolon) {
                                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                }
                                break;
                            case stnormal:
                                if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                        switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                            case sstmssqlcreateprocedure:
                                            case sstmssqlcreatefunction:
                                            case sstcreatetrigger:
                                            case sstmssqlalterprocedure:
                                            case sstmssqlalterfunction:
                                            case sstmssqlaltertrigger:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.ststoredprocedure;
                                                break;
                                            case sstmssqlbegintry:
                                            case sstmssqlbegincatch:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.sttrycatch;
                                                lctrycatchlevel = 0;
                                                break;
                                            case sstmssqlgo:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                                gst = EFindSqlStateType.stnormal;
                                                break;
                                            default:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.stsql;
                                        }
                                    } else if (ast.tokencode == 351) {
                                        this.gcurrentsqlstatement = new TMssqlBlock(this.dbVendor);
                                        this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstmssqlblock;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        gst = EFindSqlStateType.stblock;
                                    } else if (this.sqlstatements.size() == 0) {
                                        gst = EFindSqlStateType.stsql;
                                        this.gcurrentsqlstatement = new TMssqlExecute(this.dbVendor);
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    } else if (this.sqlstatements.get(this.sqlstatements.size() - 1).sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        gst = EFindSqlStateType.stsql;
                                        this.gcurrentsqlstatement = new TMssqlExecute(this.dbVendor);
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }

                                    if (!TBaseType.assigned(this.gcurrentsqlstatement)) {
                                        this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                        ast.tokentype = ETokenType.tttokenlizererrortoken;
                                        gst = EFindSqlStateType.sterror;
                                        this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                        this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }
                                } else {
                                    if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }

                                    if (TBaseType.assigned(lcprevsolidtoken) && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                        ast.tokentype = ETokenType.ttsimplecomment;
                                        ast.tokencode = 258;
                                    }
                                }
                            case stsqlplus:
                            default:
                                break;
                            case stsql:
                                if (ast.tokentype == ETokenType.ttsemicolon) {
                                    boolean lcstillinsql = false;
                                    if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif) {
                                        lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                        if (TBaseType.assigned(lcnextsolidtoken) && lcnextsolidtoken.tokencode == 349) {
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            lcstillinsql = true;
                                        }
                                    }

                                    if (!lcstillinsql) {
                                        gst = EFindSqlStateType.stnormal;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.gcurrentsqlstatement.semicolonended = ast;
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    }
                                } else if (TBaseType.assigned(lcnextsqlstatement)) {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                    } else {
                                        switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                            case sstmssqlif:
                                            case sstmssqlwhile:
                                                if (lcnextsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlbegincatch && lcnextsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlbegintry) {
                                                    if (this.gcurrentsqlstatement.dummytag == 1) {
                                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                                        if (lcnextsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlif && lcnextsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlwhile) {
                                                            this.gcurrentsqlstatement.dummytag = 0;
                                                        } else {
                                                            this.gcurrentsqlstatement.dummytag = 1;
                                                        }

                                                        prev2 = null;
                                                        continue;
                                                    }
                                                    break;
                                                }

                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.stblock;
                                                lcblocklevel = 1;
                                                prev2 = null;
                                                continue;
                                            case sstmssqlalterqueue:
                                                if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlexec) {
                                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                                    prev2 = null;
                                                    continue;
                                                }
                                                break;
                                            case sstmssqlcreateschema:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                prev2 = null;
                                                continue;
                                        }

                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                            case sstmssqlcreateprocedure:
                                            case sstmssqlcreatefunction:
                                            case sstcreatetrigger:
                                            case sstmssqlalterprocedure:
                                            case sstmssqlalterfunction:
                                            case sstmssqlaltertrigger:
                                                gst = EFindSqlStateType.ststoredprocedure;
                                                break;
                                            case sstmssqlbegintry:
                                            case sstmssqlbegincatch:
                                                gst = EFindSqlStateType.sttrycatch;
                                                lctrycatchlevel = 0;
                                                break;
                                            case sstmssqlgo:
                                                gst = EFindSqlStateType.stnormal;
                                                break;
                                            default:
                                                gst = EFindSqlStateType.stsql;
                                        }
                                    }
                                } else if (ast.tokencode == 351) {
                                    if (this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlif && this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlwhile) {
                                        if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqldeclare) {
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            this.gcurrentsqlstatement = new TMssqlBlock(this.dbVendor);
                                            this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstmssqlblock;
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            gst = EFindSqlStateType.stblock;
                                        } else {
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                        }
                                        continue;
                                    }

                                    gst = EFindSqlStateType.stblock;
                                    lcblocklevel = 0;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else if (ast.tokencode == 316) {
                                    ++case_end_nest;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else if (ast.tokencode == 313) {
                                    if (case_end_nest > 0) {
                                        --case_end_nest;
                                    }

                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else {
                                    if (ast.tokencode == 349) {
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        if ((this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif || this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlwhile) && case_end_nest == 0) {
                                            this.gcurrentsqlstatement.dummytag = 1;
                                        }
                                        continue;
                                    }

                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                                break;
                            case ststoredprocedure:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                    } else {
                                        gst = EFindSqlStateType.ststoredprocedurebody;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        prev2 = null;
                                    }
                                }

                                if (gst == EFindSqlStateType.ststoredprocedure) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    if (ast.tokencode == 351) {
                                        gst = EFindSqlStateType.stblock;
                                    }
                                }
                                break;
                            case stblock:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                    } else {
                                        prev2 = null;
                                    }
                                }

                                if (gst == EFindSqlStateType.stblock) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    if (ast.tokencode == 351) {
                                        lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                        if (TBaseType.assigned(lcnextsolidtoken)) {
                                            if (!TBaseType.mysametext(lcnextsolidtoken.astext, "tran") && !TBaseType.mysametext(lcnextsolidtoken.astext, "transaction") && !TBaseType.mysametext(lcnextsolidtoken.astext, "distributed") && !TBaseType.mysametext(lcnextsolidtoken.astext, "dialog") && !TBaseType.mysametext(lcnextsolidtoken.astext, "conversation")) {
                                                ++lcblocklevel;
                                            }
                                        } else {
                                            ++lcblocklevel;
                                        }
                                    } else if (ast.tokencode == 316) {
                                        ++lcblocklevel;
                                    } else if (ast.tokencode == 313) {
                                        boolean lcisendconversation = false;
                                        lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                        if (TBaseType.assigned(lcnextsolidtoken) && lcnextsolidtoken.tokencode == this.flexer.getkeywordvalue("conversation".toUpperCase())) {
                                            lcisendconversation = true;
                                        }

                                        if (!lcisendconversation) {
                                            if (lcblocklevel == 0) {
                                                if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif && TBaseType.assigned(lcnextsolidtoken)) {
                                                    if (lcnextsolidtoken.tokencode == 349) {
                                                        gst = EFindSqlStateType.stsql;
                                                    } else if (lcnextsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                                        TSourceToken lcnnextsolidtoken = this.sourcetokenlist.nextsolidtoken(lcnextsolidtoken.posinlist, 1, false);
                                                        if (TBaseType.assigned(lcnnextsolidtoken) && lcnnextsolidtoken.tokencode == 349) {
                                                            gst = EFindSqlStateType.stsql;
                                                        }
                                                    }
                                                }

                                                if (gst != EFindSqlStateType.stsql) {
                                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                                    gst = EFindSqlStateType.stnormal;
                                                }
                                            } else {
                                                --lcblocklevel;
                                            }
                                        }
                                    }
                                }
                                break;
                            case sttrycatch:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                    } else {
                                        if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegintry || lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlbegincatch) {
                                            ++lctrycatchlevel;
                                        }

                                        prev2 = null;
                                    }
                                }

                                if (gst != EFindSqlStateType.sttrycatch) {
                                    break;
                                }

                                this.gcurrentsqlstatement.addtokentolist(ast);
                                if ((ast.tokencode == 456 || ast.tokencode == 462) && TBaseType.assigned(lcprevsolidtoken) && lcprevsolidtoken.tokencode == 313) {
                                    if (lctrycatchlevel == 0) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                    } else {
                                        --lctrycatchlevel;
                                    }
                                }
                                break;
                            case ststoredprocedurebody:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    switch(lcnextsqlstatement.sqlstatementtype) {
                                        case sstmssqlcreateprocedure:
                                        case sstmssqlcreatefunction:
                                        case sstcreatetrigger:
                                        case sstmssqlalterprocedure:
                                        case sstmssqlalterfunction:
                                        case sstmssqlaltertrigger:
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            this.gcurrentsqlstatement = lcnextsqlstatement;
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            gst = EFindSqlStateType.ststoredprocedure;
                                            break;
                                        case sstmssqlbegintry:
                                        case sstmssqlbegincatch:
                                        case sstmssqlif:
                                        case sstmssqlwhile:
                                        case sstmssqlalterqueue:
                                        case sstmssqlcreateschema:
                                        case sstinformixCreateProcedure:
                                        case sstinformixCreateFunction:
                                        case sstinformixAlterProcedure:
                                        case sstinformixAlterFunction:
                                        case sstinformixExecute:
                                        default:
                                            prev2 = null;
                                            break;
                                        case sstmssqlgo:
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            this.gcurrentsqlstatement = lcnextsqlstatement;
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            gst = EFindSqlStateType.stnormal;
                                            break;
                                        case sstcreateview:
                                        case sstcreatetable:
                                            boolean readForNewStmt = false;
                                            TSourceToken st1 = ast.searchToken(59, -1);
                                            if (st1 != null) {
                                                TSourceToken st2 = ast.searchToken(313, -2);
                                                if (st2 != null) {
                                                    readForNewStmt = true;
                                                }
                                            }

                                            if (readForNewStmt) {
                                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                                this.gcurrentsqlstatement = lcnextsqlstatement;
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.stsql;
                                            } else {
                                                prev2 = null;
                                            }
                                    }
                                }

                                if (gst == EFindSqlStateType.ststoredprocedurebody) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                        }
                    }
                }
            }

            if (TBaseType.assigned(this.gcurrentsqlstatement) && gst != EFindSqlStateType.stnormal) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return errorcount;
        }
    }

    private TCustomSqlStatement startDaxStmt(TSourceToken currToken, TCustomSqlStatement currStmt) {
        TCustomSqlStatement newStmt = null;
        if (currToken == null) {
            return null;
        } else {
            if (currToken.tokencode == 61 && currToken.firstTokenOfLine()) {
                currToken.tokencode = 276;
                newStmt = new TDaxExprStmt(EDbVendor.dbvdax);
            } else if (currToken.tokencode == 529 && currToken.firstTokenOfLine()) {
                newStmt = new TDaxEvaluateStmt(EDbVendor.dbvdax);
                ((TDaxEvaluateStmt)newStmt).setStartWithDefine(true);
            } else if (currToken.tokencode == 530 && currToken.firstTokenOfLine()) {
                if (currStmt != null && currStmt instanceof TDaxEvaluateStmt) {
                    TDaxEvaluateStmt tmp = (TDaxEvaluateStmt)currStmt;
                    if (tmp.isStartWithDefine()) {
                        return null;
                    }
                }

                newStmt = new TDaxEvaluateStmt(EDbVendor.dbvdax);
            }

            if (newStmt == null) {
                boolean isFirst = currToken.firstTokenOfLine();
                TSourceToken prevToken = currToken.prevSolidToken();
                if (isFirst && prevToken == null) {
                    newStmt = new TDaxExprStmt(EDbVendor.dbvdax);
                }
            }

            return (TCustomSqlStatement)newStmt;
        }
    }

    int doodbcgetrawsqlstatements() {
        return this.domssqlgetrawsqlstatements();
    }

    int dodaxgetrawsqlstatements() {
        this.gcurrentsqlstatement = null;
        TCustomSqlStatement tmpStmt = null;
        EFindSqlStateType gst = EFindSqlStateType.stnormal;
        int errorcount = 0;
        int nestedParens = 0;

        for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
            TSourceToken ast = this.sourcetokenlist.get(i);
            this.sourcetokenlist.curpos = i;
            if (ast.tokencode == 531 || ast.tokencode == 532 || ast.tokencode == 533 || ast.tokencode == 534 || ast.tokencode == 535) {
                TSourceToken st1 = ast.searchToken("(", 1);
                if (st1 == null) {
                    ast.tokencode = 264;
                }
            }

            if (ast.tokencode == 40) {
                ++nestedParens;
            }

            if (ast.tokencode == 41) {
                --nestedParens;
            }

            if (nestedParens > 0) {
                if (this.gcurrentsqlstatement != null) {
                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                }
            } else {
                switch(gst) {
                    case sterror:
                        tmpStmt = this.startDaxStmt(ast, this.gcurrentsqlstatement);
                        if (tmpStmt != null) {
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = tmpStmt;
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                            gst = EFindSqlStateType.stsql;
                        } else {
                            this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        }
                        break;
                    case stnormal:
                        if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                            this.gcurrentsqlstatement = this.startDaxStmt(ast, this.gcurrentsqlstatement);
                            if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                gst = EFindSqlStateType.stsql;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            } else {
                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        } else if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                    case stsqlplus:
                    default:
                        break;
                    case stsql:
                        tmpStmt = this.startDaxStmt(ast, this.gcurrentsqlstatement);
                        if (tmpStmt != null) {
                            gst = EFindSqlStateType.stsql;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            this.gcurrentsqlstatement = tmpStmt;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                }
            }
        }

        if (TBaseType.assigned(this.gcurrentsqlstatement) && (gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.sterror)) {
            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
        }

        return errorcount;
    }

    int dohanagetrawsqlstatements() {
        int errorcount = 0;
        int case_end_nest = 0;
        if (TBaseType.assigned(this.sqlstatements)) {
            this.sqlstatements.clear();
        }

        if (!TBaseType.assigned(this.sourcetokenlist)) {
            return -1;
        } else {
            this.gcurrentsqlstatement = null;
            EFindSqlStateType gst = EFindSqlStateType.stnormal;
            int lcblocklevel = 0;
            int lctrycatchlevel = 0;
            TSourceToken lcprevsolidtoken = null;
            TSourceToken ast = null;
            int lcMergeInSelectNested = 0;
            boolean lcMergeInSelect = false;

            for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
                if (ast != null && ast.issolidtoken()) {
                    lcprevsolidtoken = ast;
                }

                ast = this.sourcetokenlist.get(i);
                this.sourcetokenlist.curpos = i;
                if (lcMergeInSelect) {
                    if (ast.tokencode == 40) {
                        ++lcMergeInSelectNested;
                    }

                    if (ast.tokencode == 41) {
                        --lcMergeInSelectNested;
                        if (lcMergeInSelectNested == 0) {
                            lcMergeInSelect = false;
                        }
                    }

                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                } else if (ast.tokenstatus == ETokenStatus.tsignoredbygetrawstatement) {
                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                } else {
                    TSourceToken st1;
                    if (ast.tokencode == 353) {
                        st1 = ast.searchToken(40, 1);
                        if (st1 == null) {
                            st1 = ast.searchToken(301, 1);
                            if (st1 == null) {
                                ast.tokencode = 264;
                            }
                        }
                    } else if (ast.tokencode == 418) {
                        st1 = ast.nextSolidToken();
                        if (st1.tokencode == 324) {
                            ast.tokencode = 287;
                        }

                        if (lcprevsolidtoken != null && lcprevsolidtoken.tokencode == 40) {
                            lcMergeInSelect = true;
                            ++lcMergeInSelectNested;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            continue;
                        }
                    } else if (ast.tokencode == 341) {
                        st1 = ast.nextSolidToken();
                        if (st1.tokencode == 534) {
                            ast.tokencode = 531;
                        }
                    } else if (ast.tokencode == 394) {
                        st1 = ast.nextSolidToken();
                        if (st1.tokencode == 262) {
                            ast.tokencode = 535;
                        }
                    } else if (ast.tokencode == 393) {
                        st1 = ast.nextSolidToken();
                        if (st1.tokencode == 262) {
                            ast.tokencode = 536;
                        }
                    } else if (ast.tokencode == 395) {
                        st1 = ast.nextSolidToken();
                        if (st1.tokencode == 262) {
                            ast.tokencode = 537;
                        }
                    } else if (ast.tokencode == 311) {
                        st1 = ast.nextSolidToken();
                        if (st1.toString().equalsIgnoreCase("structured")) {
                            ast.tokencode = 538;
                        } else if (st1.toString().equalsIgnoreCase("cache")) {
                            ast.tokencode = 539;
                        } else if (st1.toString().equalsIgnoreCase("static")) {
                            ast.tokencode = 539;
                        } else if (st1.toString().equalsIgnoreCase("dynamic")) {
                            ast.tokencode = 539;
                        } else if (st1.toString().equalsIgnoreCase("check")) {
                            ast.tokencode = 540;
                        } else if (st1.toString().equalsIgnoreCase("mask")) {
                            ast.tokencode = 546;
                        } else if (st1.toString().equalsIgnoreCase("expression")) {
                            ast.tokencode = 547;
                        }
                    } else if (ast.tokencode == 542) {
                        st1 = ast.nextSolidToken();
                        if (st1.toString().equalsIgnoreCase("priority")) {
                            ast.tokencode = 543;
                        }
                    }

                    if (gst == EFindSqlStateType.ststoredprocedurebody && ast.tokencode != 463 && ast.tokencode != 307 && ast.tokencode != 426) {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                    } else {
                        TCustomSqlStatement lcnextsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                        TSourceToken lcnextsolidtoken;
                        switch(gst) {
                            case sterror:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                    gst = EFindSqlStateType.stsql;
                                } else if (ast.tokentype == ETokenType.ttsemicolon) {
                                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else {
                                    this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                                }
                                break;
                            case stnormal:
                                if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                                    this.gcurrentsqlstatement = lcnextsqlstatement;
                                    if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                        switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                            case sstcreatetrigger:
                                            case sstcreateprocedure:
                                            case sstcreatefunction:
                                            case sstalterprocedure:
                                            case sstalterfunction:
                                            case sstaltertrigger:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.ststoredprocedure;
                                                break;
                                            case sstmssqlalterprocedure:
                                            case sstmssqlalterfunction:
                                            case sstmssqlaltertrigger:
                                            case sstmssqlbegintry:
                                            case sstmssqlbegincatch:
                                            case sstmssqlif:
                                            case sstmssqlwhile:
                                            case sstmssqlalterqueue:
                                            case sstmssqlcreateschema:
                                            case sstinformixCreateProcedure:
                                            case sstinformixCreateFunction:
                                            case sstinformixAlterProcedure:
                                            case sstinformixAlterFunction:
                                            case sstinformixExecute:
                                            case sstcreateview:
                                            case sstcreatetable:
                                            default:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                gst = EFindSqlStateType.stsql;
                                                break;
                                            case sstmssqlgo:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                                gst = EFindSqlStateType.stnormal;
                                        }
                                    } else if (ast.tokencode == 351) {
                                        this.gcurrentsqlstatement = new TMssqlBlock(this.dbVendor);
                                        this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstmssqlblock;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        gst = EFindSqlStateType.stblock;
                                    } else if (this.sqlstatements.size() == 0) {
                                        gst = EFindSqlStateType.stsql;
                                        this.gcurrentsqlstatement = new TMssqlExecute(this.dbVendor);
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    } else if (this.sqlstatements.get(this.sqlstatements.size() - 1).sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        gst = EFindSqlStateType.stsql;
                                        this.gcurrentsqlstatement = new TMssqlExecute(this.dbVendor);
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }

                                    if (!TBaseType.assigned(this.gcurrentsqlstatement)) {
                                        this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                        ast.tokentype = ETokenType.tttokenlizererrortoken;
                                        gst = EFindSqlStateType.sterror;
                                        this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                        this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }
                                } else {
                                    if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                    }

                                    if (TBaseType.assigned(lcprevsolidtoken) && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                        ast.tokentype = ETokenType.ttsimplecomment;
                                        ast.tokencode = 258;
                                    }
                                }
                            case stsqlplus:
                            case sttrycatch:
                            default:
                                break;
                            case stsql:
                                if (ast.tokentype == ETokenType.ttsemicolon) {
                                    boolean lcstillinsql = false;
                                    if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif) {
                                        lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                        if (TBaseType.assigned(lcnextsolidtoken) && lcnextsolidtoken.tokencode == 349) {
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            lcstillinsql = true;
                                        }
                                    }

                                    if (!lcstillinsql) {
                                        gst = EFindSqlStateType.stnormal;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.gcurrentsqlstatement.semicolonended = ast;
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    }
                                } else if (TBaseType.assigned(lcnextsqlstatement)) {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                    } else {
                                        switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                            case sstmssqlif:
                                            case sstmssqlwhile:
                                                if (this.gcurrentsqlstatement.dummytag == 1) {
                                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                                    if (lcnextsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlif && lcnextsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlwhile) {
                                                        this.gcurrentsqlstatement.dummytag = 0;
                                                    } else {
                                                        this.gcurrentsqlstatement.dummytag = 1;
                                                    }

                                                    st1 = null;
                                                    break;
                                                }
                                            default:
                                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                                this.gcurrentsqlstatement = lcnextsqlstatement;
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                switch(this.gcurrentsqlstatement.sqlstatementtype) {
                                                    case sstcreatetrigger:
                                                    case sstcreateprocedure:
                                                    case sstcreatefunction:
                                                    case sstalterprocedure:
                                                    case sstalterfunction:
                                                    case sstaltertrigger:
                                                        gst = EFindSqlStateType.ststoredprocedure;
                                                        continue;
                                                    default:
                                                        gst = EFindSqlStateType.stsql;
                                                        continue;
                                                }
                                            case sstcreateschema:
                                                this.gcurrentsqlstatement.addtokentolist(ast);
                                                st1 = null;
                                        }
                                    }
                                } else if (ast.tokencode == 351) {
                                    if (this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlif && this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmssqlwhile) {
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        continue;
                                    }

                                    gst = EFindSqlStateType.stblock;
                                    lcblocklevel = 0;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else if (ast.tokencode == 316) {
                                    ++case_end_nest;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else if (ast.tokencode == 313) {
                                    if (case_end_nest > 0) {
                                        --case_end_nest;
                                    }

                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                } else {
                                    if (ast.tokencode == 349) {
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        if ((this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif || this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlwhile) && case_end_nest == 0) {
                                            this.gcurrentsqlstatement.dummytag = 1;
                                        }
                                        continue;
                                    }

                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                                break;
                            case ststoredprocedure:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstalterfunction || lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstcreateprocedure || lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstcreatefunction || lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstcreatetrigger || lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstalterprocedure || lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstaltertrigger) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        gst = EFindSqlStateType.ststoredprocedure;
                                        break;
                                    }

                                    gst = EFindSqlStateType.ststoredprocedurebody;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    st1 = null;
                                }

                                if (gst == EFindSqlStateType.ststoredprocedure) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    if (ast.tokencode == 351) {
                                        gst = EFindSqlStateType.ststoredprocedurebody;
                                    } else if (ast.tokencode == 545) {
                                        gst = EFindSqlStateType.ststoredprocedurebody;
                                    }
                                }
                                break;
                            case stblock:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    if (lcnextsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlgo) {
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        this.gcurrentsqlstatement = lcnextsqlstatement;
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                        gst = EFindSqlStateType.stnormal;
                                    } else {
                                        st1 = null;
                                    }
                                }

                                if (lcblocklevel == -1 && ast.tokencode == 59) {
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    gst = EFindSqlStateType.stnormal;
                                } else if (gst == EFindSqlStateType.stblock) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    if (ast.tokencode == 351) {
                                        lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                        if (TBaseType.assigned(lcnextsolidtoken)) {
                                            if (!TBaseType.mysametext(lcnextsolidtoken.astext, "tran") && !TBaseType.mysametext(lcnextsolidtoken.astext, "transaction")) {
                                                ++lcblocklevel;
                                            }
                                        } else {
                                            ++lcblocklevel;
                                        }
                                    } else if (ast.tokencode == 316) {
                                        ++lcblocklevel;
                                    } else if (ast.tokencode == 313) {
                                        boolean lcisendconversation = false;
                                        lcnextsolidtoken = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                                        if (!lcisendconversation) {
                                            if (lcblocklevel == 0) {
                                                if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstmssqlif && TBaseType.assigned(lcnextsolidtoken)) {
                                                    if (lcnextsolidtoken.tokencode == 349) {
                                                        gst = EFindSqlStateType.stsql;
                                                    } else if (lcnextsolidtoken.tokentype == ETokenType.ttsemicolon) {
                                                        TSourceToken lcnnextsolidtoken = this.sourcetokenlist.nextsolidtoken(lcnextsolidtoken.posinlist, 1, false);
                                                        if (TBaseType.assigned(lcnnextsolidtoken) && lcnnextsolidtoken.tokencode == 349) {
                                                            gst = EFindSqlStateType.stsql;
                                                        }
                                                    }
                                                }
                                            } else {
                                                --lcblocklevel;
                                            }
                                        }
                                    }
                                }
                                break;
                            case ststoredprocedurebody:
                                if (TBaseType.assigned(lcnextsqlstatement)) {
                                    switch(lcnextsqlstatement.sqlstatementtype) {
                                        case sstcreatetrigger:
                                        case sstcreateprocedure:
                                        case sstcreatefunction:
                                        case sstalterprocedure:
                                        case sstalterfunction:
                                        case sstaltertrigger:
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            this.gcurrentsqlstatement = lcnextsqlstatement;
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            gst = EFindSqlStateType.ststoredprocedure;
                                            break;
                                        case sstmssqlalterprocedure:
                                        case sstmssqlalterfunction:
                                        case sstmssqlaltertrigger:
                                        case sstmssqlbegintry:
                                        case sstmssqlbegincatch:
                                        case sstmssqlif:
                                        case sstmssqlwhile:
                                        case sstmssqlalterqueue:
                                        case sstmssqlcreateschema:
                                        case sstinformixCreateProcedure:
                                        case sstinformixCreateFunction:
                                        case sstinformixAlterProcedure:
                                        case sstinformixAlterFunction:
                                        case sstinformixExecute:
                                        case sstcreateview:
                                        case sstcreatetable:
                                        default:
                                            st1 = null;
                                            break;
                                        case sstmssqlgo:
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            this.gcurrentsqlstatement = lcnextsqlstatement;
                                            this.gcurrentsqlstatement.addtokentolist(ast);
                                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                            gst = EFindSqlStateType.stnormal;
                                    }
                                }

                                if (gst == EFindSqlStateType.ststoredprocedurebody) {
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                        }
                    }
                }
            }

            if (TBaseType.assigned(this.gcurrentsqlstatement) && gst != EFindSqlStateType.stnormal) {
                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
            }

            return errorcount;
        }
    }

    int dosparksqlgetrawsqlstatements() {
        int errorcount = 0;
        this.gcurrentsqlstatement = null;
        EFindSqlStateType gst = EFindSqlStateType.stnormal;
        boolean waitingDelimiter = false;
        this.userDelimiterStr = this.defaultDelimiterStr;

        for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
            TSourceToken ast = this.sourcetokenlist.get(i);
            this.sourcetokenlist.curpos = i;
            TSourceToken lcprevtoken;
            int k;
            if (ast.tokencode == 394) {
                lcprevtoken = ast.nextSolidToken();
                if (lcprevtoken != null) {
                    if (lcprevtoken.tokencode == 40) {
                        ast.tokencode = 533;
                    } else if (lcprevtoken.tokencode == 262) {
                        ast.tokencode = 532;
                    }
                }
            } else if (ast.tokencode == 393) {
                lcprevtoken = ast.nextSolidToken();
                if (lcprevtoken != null && lcprevtoken.tokencode == 262) {
                    ast.tokencode = 534;
                }
            } else if (ast.tokencode == 395) {
                lcprevtoken = ast.nextSolidToken();
                if (lcprevtoken != null) {
                    if (lcprevtoken.tokencode == 262) {
                        ast.tokencode = 535;
                    } else if (lcprevtoken.tokencode == 264 && lcprevtoken.toString().startsWith("\"")) {
                        ast.tokencode = 535;
                        lcprevtoken.tokencode = 262;
                    }
                }
            } else if (ast.tokencode == 416) {
                lcprevtoken = ast.searchToken(40, 1);
                if (lcprevtoken != null) {
                    k = lcprevtoken.posinlist + 1;

                    boolean commaToken;
                    for(commaToken = false; k < ast.container.size() && ast.container.get(k).tokencode != 41; ++k) {
                        if (ast.container.get(k).tokencode == 44) {
                            commaToken = true;
                            break;
                        }
                    }

                    if (commaToken) {
                        ast.tokencode = 544;
                    }
                }
            }

            switch(gst) {
                case sterror:
                    if (ast.tokentype == ETokenType.ttsemicolon) {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        gst = EFindSqlStateType.stnormal;
                    } else {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                    }
                    break;
                case stnormal:
                    if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                        if (!ast.firstTokenOfLine() || ast.tokencode != 543 && ast.tokencode != 288) {
                            this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                            if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                ESqlStatementType[] ses = new ESqlStatementType[]{ESqlStatementType.sstmysqlcreateprocedure, ESqlStatementType.sstmysqlcreatefunction, ESqlStatementType.sstcreatetrigger};
                                if (this.includesqlstatementtype(this.gcurrentsqlstatement.sqlstatementtype, ses)) {
                                    gst = EFindSqlStateType.ststoredprocedure;
                                    waitingDelimiter = false;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.curdelimiterchar = ';';
                                } else {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            }

                            if (!TBaseType.assigned(this.gcurrentsqlstatement)) {
                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        } else {
                            gst = EFindSqlStateType.stsqlplus;
                            this.gcurrentsqlstatement = new TMySQLSource(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                    } else if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                    }
                    break;
                case stsqlplus:
                    if (ast.tokencode == 260) {
                        gst = EFindSqlStateType.stnormal;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    } else {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                    }
                    break;
                case stsql:
                    if (ast.tokentype == ETokenType.ttsemicolon && this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmysqldelimiter) {
                        gst = EFindSqlStateType.stnormal;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.gcurrentsqlstatement.semicolonended = ast;
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    } else if (ast.toString().equalsIgnoreCase(this.userDelimiterStr)) {
                        gst = EFindSqlStateType.stnormal;
                        ast.tokencode = 59;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.gcurrentsqlstatement.semicolonended = ast;
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    } else {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        if (ast.tokencode != 260 || this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmysqldelimiter) {
                            continue;
                        }

                        gst = EFindSqlStateType.stnormal;
                        this.userDelimiterStr = "";

                        for(int k1 = 0; k1 < this.gcurrentsqlstatement.sourcetokenlist.size(); ++k1) {
                            TSourceToken st = this.gcurrentsqlstatement.sourcetokenlist.get(k1);
                            if (st.tokencode != 536 && st.tokencode != 260 && st.tokencode != 259 && st.tokencode != 325) {
                                this.userDelimiterStr = this.userDelimiterStr + st.toString();
                            }
                        }

                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    }
                    break;
                case ststoredprocedure:
                    if (waitingDelimiter) {
                        if (this.userDelimiterStr.equalsIgnoreCase(ast.toString())) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            continue;
                        }

                        if (this.userDelimiterStr.startsWith(ast.toString())) {
                            String lcstr = ast.toString();

                            TSourceToken st;
                            for(k = ast.posinlist + 1; k < ast.container.size(); ++k) {
                                st = ast.container.get(k);
                                if (st.tokencode == 536 || st.tokencode == 260 || st.tokencode == 259) {
                                    break;
                                }

                                lcstr = lcstr + st.toString();
                            }

                            if (this.userDelimiterStr.equalsIgnoreCase(lcstr)) {
                                for(k = ast.posinlist; k < ast.container.size(); ++k) {
                                    st = ast.container.get(k);
                                    if (st.tokencode == 536 || st.tokencode == 260 || st.tokencode == 259) {
                                        break;
                                    }

                                    ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                                }

                                gst = EFindSqlStateType.stnormal;
                                this.gcurrentsqlstatement.semicolonended = ast;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                continue;
                            }
                        }
                    }

                    if (ast.tokencode == 351) {
                        waitingDelimiter = true;
                    }

                    if (!this.userDelimiterStr.equals(";") && waitingDelimiter) {
                        if (ast.toString().equals(this.userDelimiterStr)) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            if (ast.tokentype == ETokenType.ttsemicolon && this.userDelimiterStr.equals(";")) {
                                lcprevtoken = ast.container.nextsolidtoken(ast, -1, false);
                                if (lcprevtoken != null && lcprevtoken.tokencode == 313) {
                                    gst = EFindSqlStateType.stnormal;
                                    this.gcurrentsqlstatement.semicolonended = ast;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    continue;
                                }
                            }

                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                    } else {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }
                    }
            }
        }

        if (TBaseType.assigned(this.gcurrentsqlstatement) && (gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
        }

        return errorcount;
    }

    int domysqlgetrawsqlstatements() {
        int errorcount = 0;
        this.gcurrentsqlstatement = null;
        EFindSqlStateType gst = EFindSqlStateType.stnormal;
        boolean waitingDelimiter = false;
        this.userDelimiterStr = this.defaultDelimiterStr;

        for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
            TSourceToken ast = this.sourcetokenlist.get(i);
            this.sourcetokenlist.curpos = i;
            TSourceToken lcprevtoken;
            int k;
            TSourceToken st;
            if (ast.tokencode == 394) {
                lcprevtoken = ast.nextSolidToken();
                if (lcprevtoken != null) {
                    if (lcprevtoken.tokencode == 40) {
                        ast.tokencode = 538;
                    } else if (lcprevtoken.tokencode == 262) {
                        ast.tokencode = 539;
                    }
                }
            } else if (ast.tokencode == 393) {
                lcprevtoken = ast.nextSolidToken();
                if (lcprevtoken != null && lcprevtoken.tokencode == 262) {
                    ast.tokencode = 540;
                }
            } else if (ast.tokencode == 395) {
                lcprevtoken = ast.nextSolidToken();
                if (lcprevtoken != null) {
                    if (lcprevtoken.tokencode == 262) {
                        ast.tokencode = 553;
                    } else if (lcprevtoken.tokencode == 264 && lcprevtoken.toString().startsWith("\"")) {
                        ast.tokencode = 553;
                        lcprevtoken.tokencode = 262;
                    }
                }
            } else if (ast.tokencode == 541) {
                boolean isIdent = true;
                st = ast.nextSolidToken();
                if (st != null && st.tokencode == 40) {
                    isIdent = false;
                }

                st = ast.prevSolidToken();
                if (st != null && (st.tokencode == 542 || st.tokencode == 552)) {
                    isIdent = false;
                }

                if (isIdent) {
                    ast.tokencode = 264;
                }
            } else if (ast.tokencode == 416) {
                lcprevtoken = ast.searchToken(40, 1);
                if (lcprevtoken != null) {
                    k = lcprevtoken.posinlist + 1;

                    boolean commaToken;
                    for(commaToken = false; k < ast.container.size() && ast.container.get(k).tokencode != 41; ++k) {
                        if (ast.container.get(k).tokencode == 44) {
                            commaToken = true;
                            break;
                        }
                    }

                    if (commaToken) {
                        ast.tokencode = 544;
                    }
                }
            }

            switch(gst) {
                case sterror:
                    if (ast.tokentype == ETokenType.ttsemicolon) {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        gst = EFindSqlStateType.stnormal;
                    } else {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                    }
                    break;
                case stnormal:
                    if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                        if (ast.firstTokenOfLine() && (ast.tokencode == 543 || ast.tokencode == 288)) {
                            gst = EFindSqlStateType.stsqlplus;
                            this.gcurrentsqlstatement = new TMySQLSource(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        } else {
                            this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                            if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                ESqlStatementType[] ses = new ESqlStatementType[]{ESqlStatementType.sstmysqlcreateprocedure, ESqlStatementType.sstmysqlcreatefunction, ESqlStatementType.sstcreatetrigger};
                                if (this.includesqlstatementtype(this.gcurrentsqlstatement.sqlstatementtype, ses)) {
                                    gst = EFindSqlStateType.ststoredprocedure;
                                    waitingDelimiter = false;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.curdelimiterchar = ';';
                                } else {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            }

                            if (!TBaseType.assigned(this.gcurrentsqlstatement)) {
                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        }
                    } else if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                    }
                    break;
                case stsqlplus:
                    if (ast.tokencode == 260) {
                        gst = EFindSqlStateType.stnormal;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    } else {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                    }
                    break;
                case stsql:
                    if (ast.tokentype == ETokenType.ttsemicolon && this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmysqldelimiter) {
                        gst = EFindSqlStateType.stnormal;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.gcurrentsqlstatement.semicolonended = ast;
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    } else if (ast.toString().equalsIgnoreCase(this.userDelimiterStr)) {
                        gst = EFindSqlStateType.stnormal;
                        ast.tokencode = 59;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.gcurrentsqlstatement.semicolonended = ast;
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    } else {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        if (ast.tokencode != 260 || this.gcurrentsqlstatement.sqlstatementtype != ESqlStatementType.sstmysqldelimiter) {
                            continue;
                        }

                        gst = EFindSqlStateType.stnormal;
                        this.userDelimiterStr = "";

                        for(int k2 = 0; k2 < this.gcurrentsqlstatement.sourcetokenlist.size(); ++k2) {
                            st = this.gcurrentsqlstatement.sourcetokenlist.get(k2);
                            if (st.tokencode != 536 && st.tokencode != 260 && st.tokencode != 259 && st.tokencode != 325) {
                                this.userDelimiterStr = this.userDelimiterStr + st.toString();
                            }
                        }

                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    }
                    break;
                case ststoredprocedure:
                    if (waitingDelimiter) {
                        if (this.userDelimiterStr.equalsIgnoreCase(ast.toString())) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            continue;
                        }

                        if (this.userDelimiterStr.startsWith(ast.toString())) {
                            String lcstr = ast.toString();

                            TSourceToken st_2;
                            for(k = ast.posinlist + 1; k < ast.container.size(); ++k) {
                                st_2 = ast.container.get(k);
                                if (st_2.tokencode == 536 || st_2.tokencode == 260 || st_2.tokencode == 259) {
                                    break;
                                }

                                lcstr = lcstr + st_2.toString();
                            }

                            if (this.userDelimiterStr.equalsIgnoreCase(lcstr)) {
                                for(k = ast.posinlist; k < ast.container.size(); ++k) {
                                    st_2 = ast.container.get(k);
                                    if (st_2.tokencode == 536 || st_2.tokencode == 260 || st_2.tokencode == 259) {
                                        break;
                                    }

                                    ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                                }

                                gst = EFindSqlStateType.stnormal;
                                this.gcurrentsqlstatement.semicolonended = ast;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                continue;
                            }
                        }
                    }

                    if (ast.tokencode == 351) {
                        waitingDelimiter = true;
                    }

                    if (!this.userDelimiterStr.equals(";") && waitingDelimiter) {
                        if (ast.toString().equals(this.userDelimiterStr)) {
                            ast.tokenstatus = ETokenStatus.tsignorebyyacc;
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            if (ast.tokentype == ETokenType.ttsemicolon && this.userDelimiterStr.equals(";")) {
                                lcprevtoken = ast.container.nextsolidtoken(ast, -1, false);
                                if (lcprevtoken != null && lcprevtoken.tokencode == 313) {
                                    gst = EFindSqlStateType.stnormal;
                                    this.gcurrentsqlstatement.semicolonended = ast;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                    continue;
                                }
                            }

                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                    } else {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        if (ast.tokentype == ETokenType.ttsemicolon) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement.semicolonended = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }
                    }
            }
        }

        if (TBaseType.assigned(this.gcurrentsqlstatement) && (gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
        }

        return errorcount;
    }

    int dodb2getrawsqlstatements() {
        this.gcurrentsqlstatement = null;
        EFindSqlStateType gst = EFindSqlStateType.stnormal;
        int errorcount = 0;
        int waitingEnds = 0;
        boolean waitingForFirstBegin = false;

        for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
            TSourceToken ast = this.sourcetokenlist.get(i);
            this.sourcetokenlist.curpos = i;
            TSourceToken nextst;
            if (ast.tokencode == 315) {
                nextst = ast.searchToken("global", 1);
                if (nextst != null) {
                    ast.tokencode = 535;
                }
            } else if (ast.tokencode == 396 || ast.tokencode == 536 || ast.tokencode == 540) {
                nextst = ast.searchToken(46, 1);
                if (nextst != null) {
                    ast.tokencode = 264;
                }
            }

            switch(gst) {
                case sterror:
                    if (ast.tokentype == ETokenType.ttsemicolon) {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        gst = EFindSqlStateType.stnormal;
                    } else {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                    }
                    break;
                case stnormal:
                    if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                        if (ast.tokencode == 273) {
                            gst = EFindSqlStateType.stsqlplus;
                            this.gcurrentsqlstatement = new TSqlplusCmdStatement(this.dbVendor);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        } else {
                            this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                            if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                ESqlStatementType[] ses = new ESqlStatementType[]{ESqlStatementType.sstdb2declarecursor, ESqlStatementType.sstdb2createprocedure, ESqlStatementType.sstdb2createfunction, ESqlStatementType.sstcreatetrigger, ESqlStatementType.sst_block_with_label};
                                if (this.includesqlstatementtype(this.gcurrentsqlstatement.sqlstatementtype, ses)) {
                                    gst = EFindSqlStateType.ststoredprocedure;
                                    waitingEnds = 1;
                                    waitingForFirstBegin = true;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstdb2createprocedure || this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstdb2createfunction || this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstcreatetrigger) {
                                        this.curdelimiterchar = ';';
                                    }
                                } else if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstdb2scriptOption) {
                                    gst = EFindSqlStateType.stnormal;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                } else {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }
                            }

                            if (!TBaseType.assigned(this.gcurrentsqlstatement)) {
                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        }
                    } else if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                    }
                    break;
                case stsqlplus:
                    if (ast.insqlpluscmd) {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                    } else {
                        gst = EFindSqlStateType.stnormal;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    }
                    break;
                case stsql:
                    if (ast.tokentype == ETokenType.ttsemicolon) {
                        gst = EFindSqlStateType.stnormal;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.gcurrentsqlstatement.semicolonended = ast;
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    } else {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                    }
                    break;
                case ststoredprocedure:
                    this.nextStmt = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                    if (this.nextStmt == null || this.nextStmt.sqlstatementtype != ESqlStatementType.sstdb2createprocedure && this.nextStmt.sqlstatementtype != ESqlStatementType.sstdb2createfunction && this.nextStmt.sqlstatementtype != ESqlStatementType.sstcreatetrigger) {
                        if (this.curdelimiterchar != this.delimiterchar && (ast.tokencode == 351 || ast.tokencode == 315)) {
                            this.curdelimiterchar = this.delimiterchar;
                        }

                        if (this.curdelimiterchar == ';') {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                            if (ast.tokentype == ETokenType.ttsemicolon) {
                                gst = EFindSqlStateType.stnormal;
                                this.gcurrentsqlstatement._semicolon = ast;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            }
                        } else {
                            char ch;
                            if (ast.astext.length() == 1) {
                                ch = ast.astext.charAt(0);
                            } else if (ast.astext.length() > 1 && ast.issolidtoken()) {
                                ch = ast.astext.charAt(ast.astext.length() - 1);
                            } else {
                                ch = ' ';
                            }

                            if (ch == this.curdelimiterchar) {
                                if (ast.astext.length() > 1) {
                                    String lcstr = ast.astext.substring(0, ast.astext.length() - 1);
                                    int c;
                                    if ((c = this.flexer.getkeywordvalue(lcstr)) > 0) {
                                        ast.tokencode = c;
                                    }
                                } else {
                                    this.gcurrentsqlstatement._semicolon = ast;
                                }

                                this.gcurrentsqlstatement.addtokentolist(ast);
                                gst = EFindSqlStateType.stnormal;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            } else {
                                this.gcurrentsqlstatement.addtokentolist(ast);
                                if (ast.tokencode != 316 && ast.tokencode != 306 && ast.tokencode != 305 && ast.tokencode != 460 && ast.tokencode != 494 && ast.tokencode != 490) {
                                    if (ast.tokencode == 351) {
                                        if (waitingForFirstBegin) {
                                            waitingForFirstBegin = false;
                                        } else {
                                            ++waitingEnds;
                                        }
                                    } else if (ast.tokencode == 313) {
                                        --waitingEnds;
                                    }
                                } else {
                                    nextst = ast.nextSolidToken();
                                    if (nextst != null && nextst.tokencode != 59) {
                                        ++waitingEnds;
                                    }
                                }

                                if (ast.tokentype == ETokenType.ttsemicolon && waitingEnds == 0) {
                                    gst = EFindSqlStateType.stnormal;
                                    this.gcurrentsqlstatement._semicolon = ast;
                                    this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                }
                            }
                        }
                    } else {
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        this.gcurrentsqlstatement = this.nextStmt;
                        gst = EFindSqlStateType.ststoredprocedure;
                        waitingEnds = 1;
                        waitingForFirstBegin = true;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.curdelimiterchar = ';';
                    }
            }
        }

        if (TBaseType.assigned(this.gcurrentsqlstatement) && (gst == EFindSqlStateType.stsqlplus || gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
        }

        return errorcount;
    }

    int doteradatagetrawsqlstatements() {
        this.gcurrentsqlstatement = null;
        EFindSqlStateType gst = EFindSqlStateType.stnormal;
        int lcNestedParens = 0;
        TSourceToken lcprevst = null;
        TSourceToken lcnextst = null;
        TSourceToken lcnextst2 = null;
        TSourceToken lcprevsolidtoken = null;
        TSourceToken ast = null;
        TSourceToken lcprevst2 = null;
        int errorcount = 0;
        boolean inBTEQComment = false;
        boolean isContinueBTEQCmd = false;

        for(int i = 0; i < this.sourcetokenlist.size(); ++i) {
            if (ast != null && ast.issolidtoken()) {
                lcprevsolidtoken = ast;
            }

            ast = this.sourcetokenlist.get(i);
            this.sourcetokenlist.curpos = i;
            String lcstr;
            TSourceToken nextToken;
            if (ast.tokencode != 393 && ast.tokencode != 394 && ast.tokencode != 395) {
                if (ast.tokencode == 40) {
                    lcnextst = this.sourcetokenlist.nextsolidtoken(i, 1, false);
                    lcprevst = this.getprevsolidtoken(ast);
                    if (lcprevst != null && lcprevst.tokencode == 560) {
                        lcnextst = null;
                    }

                    if (lcprevst != null && TFunctionCall.isBuiltIn(lcprevst.toString(), EDbVendor.dbvteradata) && lcprevst.tokencode != 313 && lcprevst.tokencode != 393 && lcprevst.tokencode != 394 && lcprevst.tokencode != 395 && !lcprevst.toString().equalsIgnoreCase("current_date") && !lcprevst.toString().equalsIgnoreCase("current_time") && !lcprevst.toString().equalsIgnoreCase("current_timestamp")) {
                        lcnextst = null;
                    }

                    if (lcnextst != null) {
                        EDataType dataType = TTypeName.searchTypeByName(lcnextst.toString());
                        if (dataType != null && lcprevst != null && lcprevst.tokencode != 559) {
                            ast.tokencode = 558;
                            lcnextst = this.sourcetokenlist.nextsolidtoken(i, 2, false);
                            lcnextst2 = this.sourcetokenlist.nextsolidtoken(i, 3, false);
                            if (lcnextst != null && lcnextst2 != null) {
                                switch(dataType) {
                                    case date_t:
                                        if (lcprevst.tokencode != 262 && lcprevst.tokencode != 263 && lcprevst.tokencode != 261 && lcprevst.tokencode != 41 && lcprevst.tokencode != 561 && lcprevst.tokencode != 313 && (lcnextst.tokencode != 44 || TDatatypeAttribute.searchDataTypeAttributeByName(lcnextst2.toString()) == null)) {
                                            lcprevst2 = this.sourcetokenlist.nextsolidtoken(i, -2, false);
                                            if (lcprevst2 == null || lcprevst2.tokencode != 528) {
                                                ast.tokencode = 40;
                                            }
                                        }
                                        break;
                                    case timestamp_t:
                                    case time_t:
                                    case interval_t:
                                        if (lcnextst.tokencode == 262) {
                                            ast.tokencode = 40;
                                        }
                                        break;
                                    case period_t:
                                        ast.tokencode = 40;
                                        break;
                                    case char_t:
                                        if (lcnextst2.tokencode != 263) {
                                            ast.tokencode = 40;
                                        }
                                }
                            }
                        } else {
                            lcstr = lcnextst.toString();
                            if (lcnextst.tokencode == 321 || lcnextst.tokencode == 285) {
                                lcnextst = this.sourcetokenlist.nextsolidtoken(i, 2, false);
                                if (lcnextst != null) {
                                    lcstr = lcstr + "_" + lcnextst.toString();
                                } else {
                                    lcstr = lcstr + "_";
                                }
                            }

                            if (TDatatypeAttribute.searchDataTypeAttributeByName(lcstr) != null) {
                                ast.tokencode = 558;
                            }
                        }
                    }
                } else if (ast.tokencode == 306) {
                    nextToken = ast.searchTokenAfterObjectName();
                    if (nextToken != null && nextToken.tokencode == 341) {
                        ast.tokencode = 563;
                    }
                } else if (ast.tokencode == 564) {
                    nextToken = ast.searchToken(40, 1);
                    if (nextToken != null) {
                        ast.tokencode = 565;
                    }
                } else if (ast.tokencode == 570) {
                    nextToken = ast.searchToken(40, 1);
                    if (nextToken == null) {
                        ast.tokencode = 264;
                    }
                } else if (ast.tokencode == 559) {
                    nextToken = ast.searchToken(40, 1);
                    if (nextToken == null) {
                        if (ast.prevSolidToken() == null) {
                            ast.tokencode = 264;
                        } else if (!ast.prevSolidToken().toString().equalsIgnoreCase("anchor")) {
                            ast.tokencode = 264;
                        }
                    }
                } else if (ast.tokencode == 566) {
                    lcprevst = this.getprevsolidtoken(ast);
                    if (lcprevst != null && lcprevst.tokencode == 313) {
                        lcprevst.tokencode = 567;
                    }
                } else if (ast.tokencode == 505) {
                    nextToken = ast.searchToken(40, 1);
                    if (nextToken != null) {
                        ast.tokencode = 264;
                    }
                } else if (ast.tokencode == 350) {
                    nextToken = ast.nextSolidToken();
                    if (nextToken != null && (nextToken.tokencode == 351 || nextToken.tokencode == 313)) {
                        nextToken.tokencode = 264;
                    }
                } else if (ast.tokencode == 315) {
                    nextToken = ast.nextSolidToken();
                    if (nextToken != null) {
                        TSourceToken nextnextToken = nextToken.nextSolidToken();
                        if (nextnextToken != null) {
                            if (!nextnextToken.toString().equalsIgnoreCase("cursor") && !nextnextToken.toString().equalsIgnoreCase("insensitive") && !nextnextToken.toString().equalsIgnoreCase("scroll") && !nextnextToken.toString().equalsIgnoreCase("no")) {
                                if (nextnextToken.toString().equalsIgnoreCase("condition")) {
                                    nextToken.tokencode = 572;
                                }
                            } else {
                                nextToken.tokencode = 571;
                            }
                        }
                    }
                }
            }

            boolean readyToEnd;
            switch(gst) {
                case sterror:
                    if (ast.tokentype == ETokenType.ttsemicolon) {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        gst = EFindSqlStateType.stnormal;
                    } else {
                        this.gcurrentsqlstatement.sourcetokenlist.add(ast);
                    }
                    break;
                case stnormal:
                    if (ast.tokencode != 258 && ast.tokencode != 257 && ast.tokencode != 259 && ast.tokencode != 260 && ast.tokentype != ETokenType.ttsemicolon) {
                        if (ast.tokencode == 282) {
                            gst = ((TLexerTeradata)this.getFlexer()).cmdType(ast.astext.substring(1));
                            if (gst == EFindSqlStateType.stnormal) {
                                gst = EFindSqlStateType.stBTEQCmd;
                            }

                            this.gcurrentsqlstatement = new TTeradataBTEQCmd(this.dbVendor);
                            ((TTeradataBTEQCmd)this.gcurrentsqlstatement).setFindSqlStateType(gst);
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        } else {
                            if (ast.tokencode == 46 && ast.firstTokenOfLine()) {
                                nextToken = ast.nextSolidToken();
                                if (nextToken != null) {
                                    gst = ((TLexerTeradata)this.getFlexer()).cmdType(nextToken.astext);
                                    if (gst != EFindSqlStateType.stnormal) {
                                        this.gcurrentsqlstatement = new TTeradataBTEQCmd(this.dbVendor);
                                        ((TTeradataBTEQCmd)this.gcurrentsqlstatement).setFindSqlStateType(gst);
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        continue;
                                    }
                                }
                            } else {
                                if (ast.tokencode == 42 && ast.firstTokenOfLine()) {
                                    gst = EFindSqlStateType.stBTEQCmd;
                                    inBTEQComment = true;
                                    this.gcurrentsqlstatement = new TTeradataBTEQCmd(this.dbVendor);
                                    ((TTeradataBTEQCmd)this.gcurrentsqlstatement).setFindSqlStateType(gst);
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    continue;
                                }

                                if (ast.tokencode == 61 && ast.firstTokenOfLine()) {
                                    nextToken = ast.prevSolidToken();
                                    if (nextToken != null && nextToken.tokencode == 59) {
                                        gst = EFindSqlStateType.stBTEQCmd;
                                        this.gcurrentsqlstatement = new TTeradataBTEQCmd(this.dbVendor);
                                        ((TTeradataBTEQCmd)this.gcurrentsqlstatement).setFindSqlStateType(gst);
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        continue;
                                    }
                                } else if (ast.firstTokenOfLine()) {
                                    gst = ((TLexerTeradata)this.getFlexer()).cmdType(ast.astext);
                                    if (gst == EFindSqlStateType.stMultiLoadCmd || gst == EFindSqlStateType.stFastLoadCmd || gst == EFindSqlStateType.stFastExportCmd) {
                                        this.gcurrentsqlstatement = new TTeradataBTEQCmd(this.dbVendor);
                                        ((TTeradataBTEQCmd)this.gcurrentsqlstatement).setFindSqlStateType(gst);
                                        this.gcurrentsqlstatement.addtokentolist(ast);
                                        continue;
                                    }

                                    gst = EFindSqlStateType.stnormal;
                                }
                            }

                            this.gcurrentsqlstatement = this.sqlcmds.issql(ast, this.dbVendor, gst, this.gcurrentsqlstatement);
                            if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                                ESqlStatementType[] ses = new ESqlStatementType[]{ESqlStatementType.sstcreateprocedure, ESqlStatementType.sstteradatacreatefunction, ESqlStatementType.sstcreatetrigger};
                                if (this.includesqlstatementtype(this.gcurrentsqlstatement.sqlstatementtype, ses)) {
                                    gst = EFindSqlStateType.ststoredprocedure;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                    if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstcreateprocedure || this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstteradatacreatefunction || this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstcreatetrigger) {
                                        this.curdelimiterchar = ';';
                                    }
                                } else {
                                    gst = EFindSqlStateType.stsql;
                                    this.gcurrentsqlstatement.addtokentolist(ast);
                                }

                                lcNestedParens = 0;
                            }

                            if (!TBaseType.assigned(this.gcurrentsqlstatement)) {
                                this.syntaxErrors.add(new TSyntaxError(ast.astext, ast.lineNo, ast.columnNo < 0L ? 0L : ast.columnNo, "Error when tokenlize", EErrorType.spwarning, 10200, (TCustomSqlStatement)null, ast.posinlist));
                                ast.tokentype = ETokenType.tttokenlizererrortoken;
                                gst = EFindSqlStateType.sterror;
                                this.gcurrentsqlstatement = new TUnknownSqlStatement(this.dbVendor);
                                this.gcurrentsqlstatement.sqlstatementtype = ESqlStatementType.sstinvalid;
                                this.gcurrentsqlstatement.addtokentolist(ast);
                            }
                        }
                    } else {
                        if (TBaseType.assigned(this.gcurrentsqlstatement)) {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }

                        if (lcprevsolidtoken != null && ast.tokentype == ETokenType.ttsemicolon && lcprevsolidtoken.tokentype == ETokenType.ttsemicolon) {
                            ast.tokentype = ETokenType.ttsimplecomment;
                            ast.tokencode = 258;
                        }
                    }
                case stsqlplus:
                case stblock:
                case sttrycatch:
                case ststoredprocedurebody:
                case stExec:
                default:
                    break;
                case stsql:
                    readyToEnd = true;
                    if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstteradatacreatemacro) {
                        if (ast.tokencode == 40 || ast.tokencode == 558) {
                            ++lcNestedParens;
                        }

                        if (ast.tokencode == 41) {
                            --lcNestedParens;
                            if (lcNestedParens < 0) {
                                lcNestedParens = 0;
                            }
                        }

                        readyToEnd = lcNestedParens == 0;
                    }

                    if (ast.tokentype == ETokenType.ttsemicolon && readyToEnd) {
                        gst = EFindSqlStateType.stnormal;
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        this.gcurrentsqlstatement.semicolonended = ast;
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                    } else {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                    }
                    break;
                case ststoredprocedure:
                    readyToEnd = true;
                    if (this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstcreatetrigger) {
                        if (ast.tokencode == 40 || ast.tokencode == 558) {
                            ++lcNestedParens;
                        }

                        if (ast.tokencode == 41) {
                            --lcNestedParens;
                            if (lcNestedParens < 0) {
                                lcNestedParens = 0;
                            }
                        }

                        readyToEnd = lcNestedParens == 0;
                    }

                    if (this.curdelimiterchar != this.delimiterchar) {
                        if (ast.tokencode == 351) {
                            this.curdelimiterchar = this.delimiterchar;
                        } else if (ast.tokencode == 276 && ast.container.nextsolidtoken(ast, 1, false).tokencode == 351) {
                            this.curdelimiterchar = this.delimiterchar;
                        }
                    }

                    if (this.curdelimiterchar == ';') {
                        this.gcurrentsqlstatement.addtokentolist(ast);
                        if (ast.tokentype == ETokenType.ttsemicolon & this.gcurrentsqlstatement.sqlstatementtype == ESqlStatementType.sstcreatetrigger & readyToEnd) {
                            gst = EFindSqlStateType.stnormal;
                            this.gcurrentsqlstatement._semicolon = ast;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        }
                    } else {
                        char ch;
                        if (ast.astext.length() == 1) {
                            ch = ast.astext.charAt(0);
                        } else if (ast.astext.length() > 1) {
                            ch = ast.astext.charAt(ast.astext.length() - 1);
                        } else {
                            ch = ' ';
                        }

                        if (ast.tokencode != 257 && ast.tokencode != 258 && ch == this.curdelimiterchar && ast.firstTokenOfLine()) {
                            if (ast.astext.length() > 1) {
                                lcstr = ast.astext.substring(0, ast.astext.length() - 1);
                                int c;
                                if ((c = this.flexer.getkeywordvalue(lcstr)) > 0) {
                                    ast.tokencode = c;
                                }
                            } else {
                                this.gcurrentsqlstatement._semicolon = ast;
                            }

                            this.gcurrentsqlstatement.addtokentolist(ast);
                            gst = EFindSqlStateType.stnormal;
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        } else {
                            this.gcurrentsqlstatement.addtokentolist(ast);
                        }
                    }
                    break;
                case stMultiLoadCmd:
                    this.gcurrentsqlstatement.addtokentolist(ast);
                    if (ast.tokencode == 59 && ast.lastTokenOfLine()) {
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        gst = EFindSqlStateType.stnormal;
                    } else if (ast.lastTokenOfLine()) {
                        String cmdstr = this.gcurrentsqlstatement.sourcetokenlist.get(0).toString();
                        if (this.gcurrentsqlstatement.sourcetokenlist.size() > 1) {
                            cmdstr = cmdstr + this.gcurrentsqlstatement.sourcetokenlist.get(1).toString();
                        }

                        if (cmdstr.toLowerCase().startsWith(".if") || cmdstr.toLowerCase().startsWith(".set") || cmdstr.toLowerCase().startsWith("set")) {
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        }
                    }
                    break;
                case stFastExportCmd:
                    this.gcurrentsqlstatement.addtokentolist(ast);
                    if (ast.tokencode == 59 && ast.lastTokenOfLine()) {
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        gst = EFindSqlStateType.stnormal;
                    }
                    break;
                case stFastLoadCmd:
                    this.gcurrentsqlstatement.addtokentolist(ast);
                    if (ast.tokencode == 59 && ast.lastTokenOfLine()) {
                        this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                        gst = EFindSqlStateType.stnormal;
                    } else if (ast.tokencode == 260) {
                        nextToken = ast.nextSolidToken();
                        if (nextToken != null) {
                            if (nextToken.tokencode == 282) {
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                gst = EFindSqlStateType.stFastLoadCmd;
                            } else if (nextToken.tokencode == 46) {
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                gst = EFindSqlStateType.stFastLoadCmd;
                            } else if (this.sqlcmds.issql(nextToken, this.dbVendor, gst, this.gcurrentsqlstatement) != null) {
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                gst = EFindSqlStateType.stnormal;
                            }
                        }
                    }
                    break;
                case stBTEQCmd:
                    this.gcurrentsqlstatement.addtokentolist(ast);
                    if (ast.tokencode == 260) {
                        if (isContinueBTEQCmd) {
                            isContinueBTEQCmd = false;
                        } else {
                            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                            gst = EFindSqlStateType.stnormal;
                        }
                    } else if (ast.tokencode == 45 && ast.lastTokenOfLine()) {
                        if (!inBTEQComment) {
                            isContinueBTEQCmd = true;
                        }
                    } else if (ast.tokencode != 259) {
                        if (ast.tokencode == 42) {
                            if (inBTEQComment) {
                                inBTEQComment = false;
                                this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
                                gst = EFindSqlStateType.stnormal;
                            }

                            isContinueBTEQCmd = false;
                        } else {
                            isContinueBTEQCmd = false;
                        }
                    }
            }
        }

        if (TBaseType.assigned(this.gcurrentsqlstatement) && (gst == EFindSqlStateType.stsql || gst == EFindSqlStateType.stBTEQCmd || gst == EFindSqlStateType.ststoredprocedure || gst == EFindSqlStateType.sterror)) {
            this.doongetrawsqlstatementevent(this.gcurrentsqlstatement);
        }

        return errorcount;
    }

    int dogetrawsqlstatements() {
        this.sqlstatements.clear();
        if (this.sourcetokenlist.size() == 0) {
            return -1;
        } else {
            switch(this.dbVendor) {
                case dbvmssql:
                case dbvazuresql:
                    return this.domssqlgetrawsqlstatements();
                case dbvaccess:
                case dbvansi:
                case dbvgeneric:
                default:
                    return this.domssqlgetrawsqlstatements();
                case dbvsybase:
                    return this.dosybasegetrawsqlstatements();
                case dbvinformix:
                    return this.doinformixgetrawsqlstatements();
                case dbvoracle:
                    return this.dooraclegetrawsqlstatements();
                case dbvdb2:
                    return this.dodb2getrawsqlstatements();
                case dbvmysql:
                    return this.domysqlgetrawsqlstatements();
                case dbvteradata:
                    return this.doteradatagetrawsqlstatements();
                case dbvpostgresql:
                    return this.dopostgresqlgetrawsqlstatements();
                case dbvredshift:
                    return this.doredshiftgetrawsqlstatements();
                case dbvgreenplum:
                    return this.dogreenplumgetrawsqlstatements();
                case dbvmdx:
                    return this.domdxgetrawsqlstatements();
                case dbvnetezza:
                    return this.donetezzagetrawsqlstatements();
                case dbvhive:
                    return this.dohivegetrawsqlstatements();
                case dbvimpala:
                    return this.doimpalagetrawsqlstatements();
                case dbvhana:
                    return this.dohanagetrawsqlstatements();
                case dbvdax:
                    return this.dodaxgetrawsqlstatements();
                case dbvodbc:
                    return this.doodbcgetrawsqlstatements();
                case dbvvertica:
                    return this.doverticagetrawsqlstatements();
                case dbvopenedge:
                    return this.domssqlgetrawsqlstatements();
                case dbvcouchbase:
                    return this.docouchbasegetrawsqlstatements();
                case dbvsnowflake:
                    return this.dosnowflakegetrawsqlstatements();
                case dbvbigquery:
                    return this.dobigquerygetrawsqlstatements();
                case dbvsoql:
                    return this.dosoqlgetrawsqlstatements();
                case dbvsparksql:
                    return this.dosparksqlgetrawsqlstatements();
                case dbvathena:
                    return this.doprestogetrawsqlstatements();
                case dbvpresto:
                    return this.doprestogetrawsqlstatements();
            }
        }
    }

    public int getrawsqlstatements() {
        int ret = this.readsql();
        if (ret != 0) {
            return ret;
        } else {
            this.dosqltexttotokenlist();
            return this.dogetrawsqlstatements();
        }
    }

    public void tokenizeSqltext() {
        this.readsql();
        this.dosqltexttotokenlist();
    }

    void findAllSyntaxErrorsInPlsql(TCustomSqlStatement psql) {
        if (psql.getErrorCount() > 0) {
            this.copyerrormsg(psql);
        }

        for(int k = 0; k < psql.getStatements().size(); ++k) {
            this.findAllSyntaxErrorsInPlsql(psql.getStatements().get(k));
        }

    }

    public TSQLEnv getSqlEnv() {
        return this.sqlEnv;
    }

    public void setOnlyNeedRawParseTree(boolean onlyNeedRawParseTree) {
        this.onlyNeedRawParseTree = onlyNeedRawParseTree;
    }

    public void setSqlEnv(TSQLEnv sqlEnv) {
        this.sqlEnv = sqlEnv;
    }

    int doparse() {
        int ret = this.getrawsqlstatements();
        boolean isPushGloablStack = false;
        TStackFrame firstFrame = null;
        if (this.getFrameStack().size() == 0) {
            TGlobalScope globalScope = new TGlobalScope();
            if (this.sqlEnv == null) {
                this.sqlEnv = new TSQLEnv(this.dbVendor) {
                    public void initSQLEnv() {
                    }
                };
            }

            globalScope.setSqlEnv(this.sqlEnv);
            firstFrame = new TStackFrame(globalScope);
            firstFrame.pushStack(this.getFrameStack());
            isPushGloablStack = true;
        }

        for(int i = 0; i < this.sqlstatements.size(); ++i) {
            this.sqlstatements.get(i).setFrameStack(this.frameStack);
            int j = this.sqlstatements.get(i).parsestatement((TCustomSqlStatement)null, false, this.onlyNeedRawParseTree);
            TCustomSqlStatement sql0 = null;
            if (this.sqlstatements.get(i).isoracleplsql()) {
                sql0 = this.sqlstatements.get(i);
                this.findAllSyntaxErrorsInPlsql(sql0);
            }

            boolean doRecover = true;
            boolean ASKeyword;
            StringBuffer storedProcedure;
            int k;
            if (doRecover && (j != 0 || this.sqlstatements.get(i).getErrorCount() > 0)) {
                TCustomSqlStatement errorSqlStatement;
                if ((this.sqlstatements.get(i).sqlstatementtype == ESqlStatementType.sstcreatetable || this.sqlstatements.get(i).sqlstatementtype == ESqlStatementType.sstcreateindex && this.dbVendor != EDbVendor.dbvcouchbase) && !TBaseType.c_createTableStrictParsing) {
                    errorSqlStatement = this.sqlstatements.get(i);
                    int nested = 0;
                    ASKeyword = false;
                    boolean isFoundIgnoreToken = false;
                    TSourceToken firstIgnoreToken = null;
                    int k3 = 0;

                    while(true) {
                        if (k3 >= errorSqlStatement.sourcetokenlist.size()) {
                            if (this.dbVendor == EDbVendor.dbvoracle && firstIgnoreToken != null && !TBaseType.searchOracleTablePros(firstIgnoreToken.toString())) {
                                isFoundIgnoreToken = false;
                            }

                            if (isFoundIgnoreToken) {
                                errorSqlStatement.clearError();
                                j = this.sqlstatements.get(i).parsestatement((TCustomSqlStatement)null, false);
                            }
                            break;
                        }

                        TSourceToken st = errorSqlStatement.sourcetokenlist.get(k3);
                        if (ASKeyword) {
                            if (st.issolidtoken() && st.tokencode != 59) {
                                isFoundIgnoreToken = true;
                                if (firstIgnoreToken == null) {
                                    firstIgnoreToken = st;
                                }
                            }

                            if (st.tokencode != 59) {
                                st.tokencode = 273;
                            }
                        } else {
                            if (st.tokencode == 41) {
                                --nested;
                                if (nested == 0) {
                                    boolean isSelect = false;
                                    TSourceToken st1 = st.searchToken(341, 1);
                                    if (st1 != null) {
                                        TSourceToken st2 = st.searchToken(40, 2);
                                        if (st2 != null) {
                                            TSourceToken st3 = st.searchToken(301, 3);
                                            isSelect = st3 != null;
                                        }
                                    }

                                    if (!isSelect) {
                                        ASKeyword = true;
                                    }
                                }
                            }

                            if (st.tokencode == 40 || st.tokencode == 558) {
                                ++nested;
                            }
                        }

                        ++k3;
                    }
                }

                if ((this.sqlstatements.get(i).sqlstatementtype == ESqlStatementType.sstcreatetrigger || this.sqlstatements.get(i).sqlstatementtype == ESqlStatementType.sstdb2createfunction || this.sqlstatements.get(i).sqlstatementtype == ESqlStatementType.sstdb2createprocedure) && this.dbVendor == EDbVendor.dbvdb2) {
                    errorSqlStatement = this.sqlstatements.get(i);
                    storedProcedure = new StringBuffer(1024);

                    for(int k4 = 0; k4 < errorSqlStatement.sourcetokenlist.size(); ++k4) {
                        storedProcedure.append(errorSqlStatement.sourcetokenlist.get(k4).astext);
                    }

                    TGSqlParser lc_sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
                    lc_sqlparser.sqltext = storedProcedure.toString();
                    k = lc_sqlparser.parse();
                    if (k == 0) {
                        this.sqlstatements.remove(i);
                        this.sqlstatements.add(i, lc_sqlparser.sqlstatements.get(0));
                        continue;
                    }
                }
            }

            if (j != 0 || this.sqlstatements.get(i).getErrorCount() > 0) {
                this.copyerrormsg(this.sqlstatements.get(i));
                if (this.isEnablePartialParsing() && this.dbVendor == EDbVendor.dbvsybase && this.sqlstatements.get(i).sqlstatementtype == ESqlStatementType.sstmssqlcreateprocedure) {
                    TMssqlCreateProcedure createProcedure = (TMssqlCreateProcedure)this.sqlstatements.get(i);
                    storedProcedure = new StringBuffer(1024);
                    ASKeyword = false;

                    for(k = 0; k < createProcedure.sourcetokenlist.size(); ++k) {
                        if (!ASKeyword && createProcedure.sourcetokenlist.get(k).tokencode == 341) {
                            ASKeyword = true;
                        } else if (ASKeyword) {
                            storedProcedure.append(createProcedure.sourcetokenlist.get(k).astext);
                        }
                    }

                    TGSqlParser lc_sqlparser = new TGSqlParser(this.dbVendor);
                    lc_sqlparser.sqltext = storedProcedure.toString();
                    lc_sqlparser.parse();

                    for(int k5 = 0; k5 < lc_sqlparser.sqlstatements.size(); ++k5) {
                        createProcedure.getBodyStatements().add(lc_sqlparser.sqlstatements.get(k5));
                    }
                }
            }
        }

        if (isPushGloablStack) {
            firstFrame.popStack(this.getFrameStack());
        }

        return this.getErrorCount();
    }

    void copyerrormsg(TCustomSqlStatement sql) {
        for(int i = 0; i < sql.getSyntaxErrors().size(); ++i) {
            this.syntaxErrors.add(new TSyntaxError((TSyntaxError)sql.getSyntaxErrors().get(i)));
        }

    }

    private static String calculateLicenseKey(boolean ignoreMachineId) {
        if (userName == null) {
            return null;
        } else if (machineId == null) {
            return null;
        } else {
            byte[] bytesOfMessage = null;
            String licenseStr = "I love sql pretty printer, yeah!" + userName.toLowerCase();
            if (!ignoreMachineId) {
                licenseStr = licenseStr + machineId.toLowerCase();
            }

            try {
                bytesOfMessage = licenseStr.getBytes("UTF-8");
            } catch (UnsupportedEncodingException var6) {
                var6.printStackTrace();
            }

            MessageDigest md = null;

            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var5) {
                var5.printStackTrace();
            }

            md.digest(bytesOfMessage);
            return null;
        }
    }

    private static boolean validateLicense() {
        boolean ret = false;
        return !ret;
    }

    private static boolean check_license_time() {
        boolean ret = false;
        String toDate = "2116-11-19";
        DateFormat df = DateFormat.getDateInstance(3);
        Calendar currDtCal = Calendar.getInstance();
        currDtCal.set(11, 0);
        currDtCal.set(12, 0);
        currDtCal.set(13, 0);
        currDtCal.set(14, 0);
        Date currDt = currDtCal.getTime();

        Date toDt;
        try {
            toDt = df.parse(toDate);
        } catch (ParseException var7) {
            toDt = null;
        }

        if (toDt != null) {
            int results = toDt.compareTo(currDt);
            ret = results > 0;
        }

        return ret;
    }

    static {
        currentDBVendor = EDbVendor.dbvoracle;
        machineId = null;
        licenseOK = false;
        licenseOK = validateLicense();
    }

    static enum stored_procedure_type {
        function,
        procedure,
        package_spec,
        package_body,
        block_with_begin,
        block_with_declare,
        create_trigger,
        create_library,
        others;

        private stored_procedure_type() {
        }
    }

    static enum stored_procedure_status {
        start,
        is_as,
        body,
        bodyend,
        end;

        private stored_procedure_status() {
        }
    }
}
