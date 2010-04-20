package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPTransferType;
import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Massimiliano
 */
public class NativeTagExecutor extends TagExecutor {

    protected static final String TRUE = "true";
    protected static final String ONE = "1";
    protected static final String FIELD = "field";
    protected static final String FIELD_NAME = "fieldName";
    protected static final String OBJECT = "object";
    protected static final String ARRAY_LOCATION = "arrayLocation";
    protected static final String NAME = "name";
    protected static final String BYTE = "byte";
    protected static final String SHORT = "short";
    protected static final String INT = "int";
    protected static final String LONG = "long";
    protected static final String FLOAT = "float";
    protected static final String DOUBLE = "double";
    protected static final String CHAR = "char";
    protected static final String BOOLEAN = "boolean";
    protected static final String STANDARD_CLASS_SYMBOL = "L";
    protected static final String BYTE_CLASS_SYMBOL = "B";
    protected static final String SHORT_CLASS_SYMBOL = "S";
    protected static final String INT_CLASS_SYMBOL = "I";
    protected static final String LONG_CLASS_SYMBOL = "J";
    protected static final String FLOAT_CLASS_SYMBOL = "F";
    protected static final String DOUBLE_CLASS_SYMBOL = "D";
    protected static final String CHAR_CLASS_SYMBOL = "C";
    protected static final String BOOLEAN_CLASS_SYMBOL = "Z";
    protected static final String INSTANCE_DIRECTORY = "INSTANCE_DIRECTORY";

    protected boolean getBool(String b) {
        return b.equals(TRUE) || b.equals(ONE);
    }

    protected boolean getBool(Element expression) throws Exception {
        return ((Boolean) executeChildTag(expression)).booleanValue();
    }

  
 protected void removeLastChildNode() throws DOMException {
        Element toBeRemoved=null;
        try
        {
            toBeRemoved=DOMUtil.getLastChild(this.offlineDbgTag);
            
             this.offlineDbgTag.removeChild(toBeRemoved);
        }
        catch(Exception e)
        {
        
        }
       
    }
 
    protected void setLogic(Element target, Object value) throws Exception {
        String tag = target.getLocalName();
        if (tag.equals(FIELD)) {
            String fieldName = target.getAttribute(FIELD_NAME);
            target = DOMUtil.getFirstChild(target);
            if (target.getLocalName().equals(OBJECT)) {
                Object object = this.executeChildTag(DOMUtil.getFirstChild(target));
                object.getClass().getField(fieldName).set(object, value);
            } else {
                ((Class) this.executeChildTag(DOMUtil.getFirstChild(target))).getField(
                        fieldName).set(null, value);
            }
        } else if (tag.equals(ARRAY_LOCATION)) {
            LinkedList children = DOMUtil.getChildren(target);
            Object object = this.executeChildTag((Element) children.removeFirst());
            Element lastLocation = (Element) children.removeLast();
            Array.set(
                    children.isEmpty() ? object : getArrayLocation(object, children.iterator()),
                    getInt(lastLocation),
                    value);
        } else {
            put(target.getAttribute(NAME), value);
        }
    }

    protected int getInt(Element expression) throws Exception {
        return ((Number) this.executeChildTag(expression)).intValue();
    }

    /**
     * Gets the array location.
     *
     * @param arrayLocation the array location
     *
     * @return the array location
     *
     * @throws Exception the exception
     */
    protected Object getArrayLocation(Element arrayLocation) throws Exception {
        Iterator iterator = DOMUtil.getChildren(arrayLocation).iterator();
        return getArrayLocation(this.executeChildTag((Element) iterator.next()), iterator);
    }

    protected Element addDebugInfoForProcedureTag() throws DOMException {
        Element procedureTagForDebug;

        procedureTagForDebug = offlineDbgTag.getOwnerDocument().createElement(PROCEDURE);
        offlineDbgTag.appendChild(procedureTagForDebug);

        return procedureTagForDebug;
    }

    protected void dumpExternalFileExecutionTree(String fileName, Document execResultDoc) throws Exception {
        IVariableStore store = null;
        String instancePath = null;
        File externalFile = null;

        store = engine.getConfigurationVariablesStore();
        instancePath = (String) store.getVariable(INSTANCE_DIRECTORY);
        externalFile = new File(instancePath, fileName);

        DOMUtil.dumpXML(execResultDoc, externalFile);
    }

    protected Element getExternalFileDebugDoc() {
        Document newDocument = null;
        Element rootEl = null;
        DOMUtil util = null;

        util = new DOMUtil();

        newDocument = util.newDocument();
        rootEl = newDocument.createElement("executionResults");
        newDocument.appendChild(rootEl);

        return rootEl;
    }

    protected FTPClient getFTPClient(Element ftp) throws Exception {
        Element hostTag;
        Element portTag;
        Element usernameTag;
        Element passwordTag;
        Document doc;
        Iterator children = DOMUtil.getChildren(ftp).iterator();

        doc = this.offlineDbgTag.getOwnerDocument();

        hostTag = doc.createElement("host");
        portTag = doc.createElement("port");
        usernameTag = doc.createElement("username");
        passwordTag = doc.createElement("password");
        this.offlineDbgTag.appendChild(hostTag);
        this.offlineDbgTag.appendChild(portTag);
        this.offlineDbgTag.appendChild(usernameTag);
        this.offlineDbgTag.appendChild(passwordTag);

        FTPClient ftpClient = new FTPClient((String) this.executeChildTag(DOMUtil.getFirstChild((Element) children.next()), hostTag),
                ((Integer) executeChildTag(DOMUtil.getFirstChild((Element) children.next()), portTag)).intValue());

        ftpClient.login(
                (String) executeChildTag(DOMUtil.getFirstChild((Element) children.next()), usernameTag),
                (String) executeChildTag(DOMUtil.getFirstChild((Element) children.next()), passwordTag));

        ftpClient.setType(ftp.getAttribute(TRANSFER).equals(ASCII) ? FTPTransferType.ASCII : FTPTransferType.BINARY);

        if (DOMUtil.getBool(ftp.getAttribute("pasv"))) {
            ftpClient.setConnectMode(FTPConnectMode.PASV);
        } else {
            ftpClient.setConnectMode(FTPConnectMode.ACTIVE);
        }
        return ftpClient;
    }

    protected Object getArrayLocation(Object object, Iterator indexes) throws
            Exception {
        do {
            object = Array.get(object, getInt((Element) indexes.next()));
        } while (indexes.hasNext());
        return object;
    }

    /**
     * Sets the arguments.
     *
     * @param arguments the arguments
     *
     * @throws Exception the exception
     */
    protected void setArguments(Iterator arguments) throws Exception {
        Element argument;
        while (arguments.hasNext()) {
            argument = (Element) arguments.next();
            put(argument.getAttribute(NAME), this.executeChildTag(DOMUtil.getFirstChild(argument)));
        }
    }

    /**
     * Gets the class name.
     *
     * @param c the c
     *
     * @return the class name
     */
    protected static final String getClassName(Class c) {
        String className = c.getName();
        if (c.isArray()) {
            return className;
        }
        if (!c.isPrimitive()) {
            return STANDARD_CLASS_SYMBOL + className;
        }
        if (className.equals(BYTE)) {
            return BYTE_CLASS_SYMBOL;
        }
        if (className.equals(SHORT)) {
            return SHORT_CLASS_SYMBOL;
        }
        if (className.equals(INT)) {
            return INT_CLASS_SYMBOL;
        }
        if (className.equals(LONG)) {
            return LONG_CLASS_SYMBOL;
        }
        if (className.equals(FLOAT)) {
            return FLOAT_CLASS_SYMBOL;
        }
        if (className.equals(DOUBLE)) {
            return DOUBLE_CLASS_SYMBOL;
        }
        if (className.equals(CHAR)) {
            return CHAR_CLASS_SYMBOL;
        }
        if (className.equals(BOOLEAN)) {
            return BOOLEAN_CLASS_SYMBOL;
        }
        return null;
    }

    /**
     * Adds the attribute.
     *
     * @param element the element
     * @param attributeDescriptor the attribute descriptor
     *
     * @throws Exception the exception
     */
    protected void addAttribute(Element element, Element attributeDescriptor) throws
            Exception {
        String localName = attributeDescriptor.getLocalName();
        if (localName.equals(ATTRIBUTE)) {
            element.setAttribute(attributeDescriptor.getAttribute(NAME),
                    String.valueOf(this.executeChildTag(DOMUtil.getFirstChild(attributeDescriptor))));
        } else if (localName.equals(NAMESPACE)) {
            String prefix = attributeDescriptor.getAttribute(PREFIX);
            element.setAttribute(prefix.length() == 0 ? XMLNS : XMLNS_ + prefix,
                    attributeDescriptor.getAttribute(URI));
        } else if (localName.equals(IF)) {
            Iterator children = DOMUtil.getChildren(attributeDescriptor).
                    iterator();
            boolean flag = getBool((Element) children.next());
            Element then = (Element) children.next();
            if (flag) {
                addAttribute(element, then);
            } else if (children.hasNext()) {
                addAttribute(element, (Element) children.next());
            }
        }
    }

    /**
     * Process store.
     *
     * @param store the store
     *
     * @throws Exception the exception
     */
    protected void processStore(Element store) throws Exception {
        String localName = store.getLocalName();
        if (localName.equals(STORE_ELEMENT)) {
            put(store.getAttribute(NAME), DOMUtil.getFirstChild(store));
        } else {
            put(store.getAttribute(NAME), store.getChildNodes());
        }
    }

    /**
     * Builds the root.
     *
     * @param document the document
     * @param rootDescriptor the root descriptor
     *
     * @throws Exception the exception
     */
    protected void buildRoot(Document document, Element rootDescriptor) throws
            Exception {
        IVariableStore varStore;
        String namespaceURI = rootDescriptor.getNamespaceURI();

        varStore = this.engine.getVariablesStore();

        if (namespaceURI != null && namespaceURI.equals(XML_SCRIPT_NAMESPACE)) {
            String localName = rootDescriptor.getLocalName();
            if (localName.equals(ELEMENT)) {
                Element tagElement = DOMUtil.getFirstChild(rootDescriptor);
                String uri = tagElement.getAttribute(URI);
                String tag = String.valueOf(this.executeChildTag(DOMUtil.getFirstChild(
                        tagElement)));
//                Element root = uri.length() == 0 ? document.createElement(tag) : document.createElementNS(uri, tag);
                Element root = document.createElementNS(uri, tag);
                document.appendChild(root);
                fillElement(root, rootDescriptor);
            } else if (localName.equals(IF)) {
                Iterator children = DOMUtil.getChildren(rootDescriptor).
                        iterator();
                boolean flag = getBool((Element) children.next());
                Element then = (Element) children.next();
                if (flag) {
                    buildRoot(document, then);
                } else {
                    buildRoot(document, (Element) children.next());
                }
            } else if (localName.equals(STORED_ELEMENT)) {
                buildRoot(document,
                        (Element) varStore.getVariable(rootDescriptor.getAttribute(NAME)));
            } else {
                Element root = (Element) document.importNode(rootDescriptor, false);
                document.appendChild(root);
                addContent(root, rootDescriptor.getChildNodes());
            }
        } else {
            Element root = (Element) document.importNode(rootDescriptor, false);
            document.appendChild(root);
            addContent(root, rootDescriptor.getChildNodes());
        }
    }

    /**
     * Fill element.
     *
     * @param element the element
     * @param descriptor the descriptor
     *
     * @throws Exception the exception
     */
    protected void fillElement(Element element, Element descriptor) throws
            Exception {
        Iterator children = DOMUtil.getChildren(descriptor).iterator();
        children.next();
        Iterator attributes = DOMUtil.getChildren((Element) children.next()).
                iterator();
        while (attributes.hasNext()) {
            addAttribute(element, (Element) attributes.next());
        }
        addContent(element, ((Element) children.next()).getChildNodes());
    }

    /**
     * Adds the content.
     *
     * @param element the element
     * @param childDescriptors the child descriptors
     *
     * @throws Exception the exception
     */
    protected void addContent(Element element, NodeList childDescriptors) throws
            Exception {
        for (int index = 0; index < childDescriptors.getLength();
                appendChild(element, childDescriptors.item(index++))) {
            ;
        }
    }

    /**
     * Append child.
     *
     * @param element the element
     * @param childNode the child node
     *
     * @throws Exception the exception
     */
    protected void appendChild(Element element, Node childNode) throws Exception {
        IVariableStore varStore;

        varStore = this.engine.getVariablesStore();

        Document document = element.getOwnerDocument();
        if (childNode.getNodeType() != Node.ELEMENT_NODE) {
            if (childNode.getNodeType() == Node.TEXT_NODE) {
                element.appendChild(document.createTextNode(childNode.getNodeValue()));
            }
            return;
        }
        Element childDescriptor = (Element) childNode;
        String namespaceURI = childDescriptor.getNamespaceURI();
        if (namespaceURI != null && namespaceURI.equals(XML_SCRIPT_NAMESPACE)) {
            String localName = childDescriptor.getLocalName();
            if (localName.equals(TEXT)) {
                element.appendChild(document.createTextNode(
                        String.valueOf(this.executeChildTag(DOMUtil.getFirstChild(
                        childDescriptor)))));
            } else if (localName.equals(ELEMENT)) {
                Element tagElement = DOMUtil.getFirstChild(childDescriptor);
                String uri = tagElement.getAttribute(URI);
                String tag = String.valueOf(executeChildTag(DOMUtil.getFirstChild(
                        tagElement)));
                Element child = document.createElementNS(uri, tag);
                element.appendChild(child);
                fillElement(child, childDescriptor);
            } else if (localName.equals(STORE_ELEMENT) ||
                    localName.equals(STORE_FRAGMENT)) {
                processStore(childDescriptor);
            } else if (localName.equals(STORED_ELEMENT)) {
                appendChild(element,
                        (Element) varStore.getVariable(childDescriptor.getAttribute(NAME)));
            } else if (localName.equals(STORED_FRAGMENT)) {
                addContent(element,
                        (NodeList) varStore.getVariable(childDescriptor.getAttribute(NAME)));
            } else if (localName.equals(IF)) {
                Iterator children = DOMUtil.getChildren(childDescriptor).
                        iterator();
                boolean flag = getBool((Element) children.next());
                Element then = (Element) children.next();
                if (flag) {
                    addContent(element, then.getChildNodes());
                } else if (children.hasNext()) {
                    addContent(element,
                            ((Element) children.next()).getChildNodes());
                }
            } else if (localName.equals(REPEAT)) {
                Iterator children = DOMUtil.getChildren(childDescriptor).
                        iterator();
                Element condition = (Element) children.next();
                NodeList content = ((Element) children.next()).getChildNodes();
                while (getBool(condition)) {
                    addContent(element, content);
                }
            } else {
                this.executeChildTag(childDescriptor);
            }
        } else {
            Element child = (Element) document.importNode(childDescriptor, false);
            element.appendChild(child);
            addContent(child, childDescriptor.getChildNodes());
        }
    }

    /**
     * This method is used to download files under a specified directory
     *
     *
     * @param client FTP client
     * @param remotePath Remote directory or file
     * @param localPath local directory or file
     * @throws java.lang.Exception Exception thrown in case of error
     */
    protected void downloadDir(FTPClient client, String remotePath, String localPath) throws Exception {
        /*  String[] dirListing,dirListingFull;
        dirListingFull=client.dir(remotePath,true);
        dirListing=client.dir(remotePath);
        
        //files may not be sorted in the same way
        
        for(int i=0;i<dirListing.length;i++)
        {
        if(dirListingFull[i].startsWith("d"))
        {
        File dir=new File(localPath,dirListing[i].substring(dirListing[i].lastIndexOf("/")+1));
        dir.mkdir();
        downloadDir(client,dirListing[i],dir.getAbsolutePath());
        }
        else
        {
        String fileName=dirListing[i].substring(dirListing[i].lastIndexOf("/")+1);
        if(remotePath.endsWith(fileName)==true)
        {
        //the ftpGet tag has been called in order to download a single file
        client.get(localPath,remotePath);
        }
        else
        {
        File file=new File(localPath,fileName);
        
        client.get(file.getAbsolutePath(),dirListing[i]);
        }
        }
        
        }*/

        String[] dirListingFull;
        dirListingFull = client.dir(remotePath, true);
        for (int i = 0; i < dirListingFull.length; i++) {
            if (dirListingFull[i].startsWith("d")) {
                File dir = new File(localPath, dirListingFull[i].substring(dirListingFull[i].lastIndexOf(" ") + 1));
                dir.mkdir();

                String rmtPath = remotePath + "/" + dirListingFull[i].substring(dirListingFull[i].lastIndexOf(" ") + 1);
                downloadDir(client, rmtPath, dir.getAbsolutePath());
            } else {
                String fileName = dirListingFull[i].substring(dirListingFull[i].lastIndexOf(" ") + 1);
                if (remotePath.endsWith(fileName) == true) {
                    //the ftpGet tag has been called in order to download a single file
                    client.get(localPath, remotePath);
                } else {
                    File file = new File(localPath, fileName);

                    client.get(file.getAbsolutePath(), remotePath + "/" + dirListingFull[i].substring(dirListingFull[i].lastIndexOf(" ") + 1));
                }
            }

        }



    }

    /**
     * Put.
     *
     * @param key the key
     * @param value the value
     */
    protected void put(Object key, Object value) {
        IVariableStore store = null;

        store = engine.getVariablesStore();

        store.setVariable(key, value == null ? NULL_MARKER : value);
    }

    /**
     * Get.
     *
     * @param key the key
     *
     * @return the object
     */
    protected Object get(Object key) {
        IVariableStore store = null;

        store = engine.getVariablesStore();

        Object value = store.getVariable(key);
        return value == NULL_MARKER ? null : value;
    }
    protected static final String NULL = "null";
    /**
     * The Constant CLASS_LITERAL.
     */
    protected static final String CLASS_LITERAL = "classLiteral";
    /**
     * The Constant PRIMITIVE_TYPE.
     */
    protected static final String PRIMITIVE_TYPE = "primitiveType";
    /**
     * The Constant ARRAY_TYPE.
     */
    protected static final String ARRAY_TYPE = "arrayType";
    /**
     * The Constant LITERAL.
     */
    protected static final String LITERAL = "literal";
    /**
     * The Constant CHARACTER.
     */
    protected static final String CHARACTER = "character";
    /**
     * The Constant CHARACTER.
     */
    protected static final String DATESTRINGFORMATTER = "dateStringFormatter";
    /**
     * The Constant NOW.
     */
    protected static final String NOW = "now";
    /**
     * The Constant IS_BEFORE.
     */
    protected static final String IS_BEFORE = "isBefore";
    /**
     * The Constant DATE.
     */
    protected static final String DATE = "date";
    /**
     * The Constant ZIP.
     */
    protected static final String ZIP = "zip";
    /**
     * The Constant STRING.
     */
    protected static final String STRING = "string";
    /**
     * The Constant STRING_LENGTH.
     */
    protected static final String STRING_LENGTH = "stringLength";
    /**
     * The Constant VARIABLE.
     */
    protected static final String VARIABLE = "variable";
    /**
     * The Constant VARIABLE.
     */
    protected static final String FORMAT = "format";
    /**
     * The Constant EXISTS_VARIABLE.
     */
    protected static final String EXISTS_VARIABLE = "existsVariable";
    /**
     * The Constant ARRAY_LENGTH.
     */
    protected static final String ARRAY_LENGTH = "arrayLength";
    /**
     * The Constant METHOD_INVOCATION.
     */
    protected static final String METHOD_INVOCATION = "methodInvocation";
    /**
     * The Constant NEW_OBJECT.
     */
    protected static final String NEW_OBJECT = "newObject";
    /**
     * The Constant NEW_INTERNAL_OBJECT.
     */
    protected static final String NEW_INTERNAL_OBJECT = "newInternalObject";
    /**
     * The Constant NEW_TYPED_ARRAY.
     */
    protected static final String NEW_TYPED_ARRAY = "newTypedArray";
    /**
     * The Constant NEW_ARRAY.
     */
    protected static final String NEW_ARRAY = "newArray";
    /**
     * The Constant NEW_INITIALIZED_TYPED_ARRAY.
     */
    protected static final String NEW_INITIALIZED_TYPED_ARRAY =
            "newInitializedTypedArray";
    /**
     * The Constant NEW_INITIALIZED_ARRAY.
     */
    protected static final String NEW_INITIALIZED_ARRAY = "newInitializedArray";
    /**
     * The Constant STRING_CAT.
     */
    protected static final String STRING_CAT = "stringCat";
    /**
     * The Constant ATOI.
     */
    protected static final String ATOI = "atoi";
    /**
     * The Constant ITOA.
     */
    protected static final String ITOA = "itoa";
    /**
     * The Constant SLEEP.
     */
    protected static final String SLEEP = "sleep";
    /**
     * The Constant AMOUNT.
     */
    protected static final String AMOUNT = "amount";
    /**
     * The Constant PLUS.
     */
    protected static final String PLUS = "plus";
    /**
     * The Constant MINUS.
     */
    protected static final String MINUS = "minus";
    /**
     * The Constant MULTIPLY.
     */
    protected static final String MULTIPLY = "multiply";
    /**
     * The Constant DIVIDE.
     */
    protected static final String DIVIDE = "divide";
    /**
     * The Constant MODULE.
     */
    protected static final String MODULE = "module";
    /**
     * The Constant INC.
     */
    protected static final String INC = "inc";
    /**
     * The Constant DEC.
     */
    protected static final String DEC = "dec";
    protected static final String FORK = "fork";
    /**
     * The Constant NEGATE.
     */
    protected static final String NEGATE = "negate";
    /**
     * The Constant L_SHIFT.
     */
    protected static final String L_SHIFT = "lShift";
    /**
     * The Constant R_S_SHIFT.
     */
    protected static final String R_S_SHIFT = "rSShift";
    /**
     * The Constant R_U_SHIFT.
     */
    protected static final String R_U_SHIFT = "rUShift";
    /**
     * The Constant BIT_AND.
     */
    protected static final String BIT_AND = "bitAnd";
    /**
     * The Constant BIT_OR.
     */
    protected static final String BIT_OR = "bitOr";
    /**
     * The Constant BIT_XOR.
     */
    protected static final String BIT_XOR = "bitXor";
    /**
     * The Constant NOT.
     */
    protected static final String NOT = "not";
    /**
     * The Constant GT.
     */
    protected static final String GT = "gt";
    /**
     * The Constant GTE.
     */
    protected static final String GTE = "gte";
    /**
     * The Constant LT.
     */
    protected static final String LT = "lt";
    /**
     * The Constant LTE.
     */
    protected static final String LTE = "lte";
    /**
     * The Constant EQ.
     */
    protected static final String EQ = "eq";
    /**
     * The Constant NEQ.
     */
    protected static final String NEQ = "neq";
    /**
     * The Constant OEQ.
     */
    protected static final String OEQ = "oeq";
    /**
     * The Constant IS_NULL.
     */
    protected static final String IS_NULL = "isNull";
    /**
     * The Constant IS_NOT_NULL.
     */
    protected static final String IS_NOT_NULL = "isNotNull";
    /**
     * The Constant ONEQ.
     */
    protected static final String ONEQ = "oneq";
    /**
     * The Constant SHORTCUT.
     */
    protected static final String SHORTCUT = "shortcut";
    /**
     * The Constant AND.
     */
    protected static final String AND = "and";
    /**
     * The Constant OR.
     */
    protected static final String OR = "or";
    /**
     * The Constant XOR.
     */
    protected static final String XOR = "xor";
    /**
     * The Constant SEQUENCE.
     */
    protected static final String SEQUENCE = "sequence";
    /**
     * The Constant IF.
     */
    protected static final String IF = "if";
    /**
     * The Constant WHILE.
     */
    protected static final String WHILE = "while";
    /**
     * The Constant FOR.
     */
    protected static final String FOR = "for";
    /**
     * The Constant FOR_CONDITION.
     */
    protected static final String FOR_CONDITION = "condition";
    /**
     * The Constant PRINT.
     */
    protected static final String PRINT = "print";
    /**
     * The Constant NEW_LINE.
     */
    protected static final String NEW_LINE = "newLine";
    /**
     * The Constant EXECUTE.
     */
    protected static final String EXECUTE = "execute";
    /**
     * The Constant STORE_PROCEDURE.
     */
    protected static final String STORE_PROCEDURE = "storeProcedure";
    /**
     * The Constant LOAD_PROCEDURE.
     */
    protected static final String LOAD_PROCEDURE = "loadProcedure";
    /**
     * The Constant CALL.
     */
    protected static final String CALL = "call";
    /**
     * The Constant ARGUMENT.
     */
    protected static final String ARGUMENT = "argument";
    /**
     * The Constant PROCEDURE.
     */
    protected static final String PROCEDURE = "procedure";
    /**
     * The Constant PROCEDURE_FILE.
     */
    protected static final String PROCEDURE_FILE = "procedureFile";
    /**
     * The Constant LOAD_TAG_INTERPRETER.
     */
    protected static final String LOAD_TAG_INTERPRETER = "loadTagInterpreter";
    /**
     * The Constant EXTERN.
     */
    protected static final String EXTERN = "extern";
    /**
     * The Constant INTERPRETER.
     */
    protected static final String INTERPRETER = "interpreter";
    /**
     * The Constant IMPORT.
     */
    protected static final String IMPORT = "import";
    /**
     * The Constant ERROR.
     */
    protected static final String ERROR = "error";
    /**
     * The Constant SET.
     */
    protected static final String SET = "set";
    /**
     * The Constant SET_VARIABLE.
     */
    protected static final String SET_VARIABLE = "setVariable";
    /**
     * The Constant SET_TEXT.
     */
    protected static final String SET_TEXT = "setText";
    /**
     * The Constant NEW.
     */
    protected static final String NEW = "new";
    /**
     * The Constant TEXT.
     */
    protected static final String TEXT = "text";
    /**
     * The Constant HORIZONTAL_MOVE.
     */
    protected static final String HORIZONTAL_MOVE = "horizontalMove";
    /**
     * The Constant VERTICAL_MOVE.
     */
    protected static final String VERTICAL_MOVE = "verticalMove";
    /**
     * The Constant ABSOLUTE.
     */
    protected static final String ABSOLUTE = "absolute";
    /**
     * The Constant START.
     */
    protected static final String START = "start";
    /**
     * The Constant END.
     */
    protected static final String END = "end";
    /**
     * The Constant POSITION.
     */
    protected static final String POSITION = "position";
    /**
     * The Constant FIELD_MOVE.
     */
    protected static final String FIELD_MOVE = "fieldMove";
    /**
     * The Constant SEPARATORS.
     */
    protected static final String SEPARATORS = "separators";
    /**
     * The Constant GREEDY.
     */
    protected static final String GREEDY = "greedy";
    /**
     * The Constant SEARCH.
     */
    protected static final String SEARCH = "search";
    /**
     * The Constant GOTO_LINE_START.
     */
    protected static final String GOTO_LINE_START = "gotoLineStart";
    /**
     * The Constant GOTO_LINE_END.
     */
    protected static final String GOTO_LINE_END = "gotoLineEnd";
    /**
     * The Constant GOTO_FILE_START.
     */
    protected static final String GOTO_FILE_START = "gotoFileStart";
    /**
     * The Constant GOTO_FILE_END.
     */
    protected static final String GOTO_FILE_END = "gotoFileEnd";
    /**
     * The Constant MARK.
     */
    protected static final String MARK = "mark";
    /**
     * The Constant GOTO.
     */
    protected static final String GOTO = "goto";
    /**
     * The Constant EXTRACT.
     */
    protected static final String EXTRACT = "extract";
    /**
     * The Constant LINE_NUMBER.
     */
    protected static final String LINE_NUMBER = "lineNumber";
    /**
     * The Constant COLUMN_NUMBER.
     */
    protected static final String COLUMN_NUMBER = "columnNumber";
    /**
     * The Constant XML.
     */
    protected static final String XML = "xml";
    /**
     * The Constant ATTRIBUTE_PREFIX.
     */
    protected static final String ATTRIBUTE_PREFIX = "attributePrefix";
    /**
     * The Constant TEXT_TAG.
     */
    protected static final String TEXT_TAG = "textTag";
    /**
     * The Constant FTP_GET.
     */
    protected static final String FTP_GET = "ftpGet";
    /**
     * The Constant FTP_PUT.
     */
    protected static final String FTP_PUT = "ftpPut";
    /**
     * The Constant FTP_DELETE.
     */
    protected static final String FTP_DELETE = "ftpDelete";
    /**
     * The Constant FTP_EXISTS.
     */
    protected static final String FTP_EXISTS = "ftpExists";
    /**
     * The Constant FTP_ACCOUNT.
     */
    protected static final String FTP_ACCOUNT = "ftpAccount";
    /**
     * The Constant DURATION.
     */
    protected static final String DURATION = "duration";
    /**
     * The Constant TIMER.
     */
    protected static final String TIMER = "timer";
    /**
     * The Constant BASE64.
     */
    protected static final String BASE64 = "base64";
    /**
     * The Constant DECODE.
     */
    protected static final String DECODE = "decode";
    /**
     * The Constant ENCODE.
     */
    protected static final String ENCODE = "encode";
    /**
     * The Constant DIRECTION.
     */
    protected static final String DIRECTION = "direction";
    /**
     * The Constant LOAD_XML.
     */
    protected static final String LOAD_XML = "loadXML";
    /**
     * The Constant VALIDATING_SCHEMA.
     */
    protected static final String VALIDATING_SCHEMA = "validatingSchema";
    /**
     * The Constant DUMP_XML.
     */
    protected static final String DUMP_XML = "dumpXML";
    /**
     * The Constant XML_REQUEST.
     */
    protected static final String XML_REQUEST = "xmlRequest";

    protected static final String SOAP_REQUEST = "soapRequest";
    /**
     * The Constant ORDER_ID.
     */
    protected static final String ORDER_ID = "orderId";
    /**
     * The Constant MASS_HOST.
     */
    protected static final String MASS_HOST = "massHost";
    /**
     * The Constant MASS_HOST_ADDRESS.
     */
    protected static final String MASS_HOST_ADDRESS = "massHostAddress";
    /**
     * The Constant XPATH.
     */
    protected static final String XPATH = "xPath";
    /**
     * The Constant HTTP.
     */
    protected static final String HTTP = "http";
    /**
     * The Constant METHOD.
     */
    protected static final String METHOD = "method";
    /**
     * The Constant POST.
     */
    protected static final String POST = "POST";
    /**
     * The Constant HOST.
     */
    protected static final String HOST = "host";
    /**
     * The Constant PORT.
     */
    protected static final String PORT = "port";
    /**
     * The Constant LOAD_FILE.
     */
    protected static final String LOAD_FILE = "loadFile";
    /**
     * The Constant DUMP_FILE.
     */
    protected static final String DUMP_FILE = "dumpFile";
    /**
     * The Constant BINARY.
     */
    protected static final String BINARY = "binary";
    /**
     * The Constant FILE_TYPE.
     */
    protected static final String FILE_TYPE = "fileType";
    /**
     * The Constant FILE_EXISTS.
     */
    protected static final String FILE_EXISTS = "fileExists";
    /**
     * The Constant FILE_DELETE.
     */
    protected static final String FILE_DELETE = "fileDelete";
    /**
     * The Constant MKDIR.
     */
    protected static final String MKDIR = "mkdir";
    /**
     * The Constant RMDIR.
     */
    protected static final String RMDIR = "rmdir";
    /**
     * The Constant USER.
     */
    protected static final String USER = "user";
    /**
     * The Constant PASSWORD.
     */
    protected static final String PASSWORD = "password";
    /**
     * The Constant ACTION.
     */
    protected static final String ACTION = "action";
    /**
     * The Constant TRANSFER.
     */
    protected static final String TRANSFER = "transfer";
    /**
     * The Constant LOCAL_PATH.
     */
    protected static final String LOCAL_PATH = "localPath";
    /**
     * The Constant REMOTE_PATH.
     */
    protected static final String REMOTE_PATH = "remotePath";
    /**
     * The Constant ASCII.
     */
    protected static final String ASCII = "ascii";
    /**
     * The Constant XSLT.
     */
    protected static final String XSLT = "xslt";
    /**
     * The Constant XML_OUTPUT.
     */
    protected static final String XML_OUTPUT = "xmlOutput";
    /**
     * The Constant COMMAND.
     */
    protected static final String COMMAND = "command";
    /**
     * The Constant ASYNCHRONOUS.
     */
    protected static final String ASYNCHRONOUS = "asynchronous";
    /**
     * The Constant SYNCHRONOUS.
     */
    protected static final String SYNCHRONOUS = "synchronous";
    /**
     * The Constant LOG.
     */
    protected static final String LOG = "log";
    /**
     * The Constant LEVEL.
     */
    protected static final String LEVEL = "level";
    /**
     * The Constant LOCAL_HOST.
     */
    protected static final String LOCAL_HOST = "localHost";
    /**
     * The Constant XML_DOCUMENT.
     */
    protected static final String XML_DOCUMENT = "xmlDocument";
    /**
     * The Constant XML_SCRIPT_NAMESPACE.
     */
    protected static final String XML_SCRIPT_NAMESPACE =
            "http://pisa.intecs.it/mass/toolbox/xmlScript";
    /**
     * The Constant STORE_ELEMENT.
     */
    protected static final String STORE_ELEMENT = "storeElement";
    /**
     * The Constant STORE_FRAGMENT.
     */
    protected static final String STORE_FRAGMENT = "storeFragment";
    /**
     * The Constant STORED_ELEMENT.
     */
    protected static final String STORED_ELEMENT = "storedElement";
    /**
     * The Constant STORED_FRAGMENT.
     */
    protected static final String STORED_FRAGMENT = "storedFragment";
    /**
     * The Constant ELEMENT.
     */
    protected static final String ELEMENT = "element";
    /**
     * The Constant TAG.
     */
    protected static final String TAG = "tag";
    /**
     * The Constant NAMESPACE.
     */
    protected static final String NAMESPACE = "namespace";
    /**
     * The Constant PREFIX.
     */
    protected static final String PREFIX = "prefix";
    /**
     * The Constant XMLNS.
     */
    protected static final String XMLNS = "xmlns";
    /**
     * The Constant XMLNS_.
     */
    protected static final String XMLNS_ = "xmlns:";
    /**
     * The Constant URI.
     */
    protected static final String URI = "uri";
    /**
     * The Constant ATTRIBUTE.
     */
    protected static final String ATTRIBUTE = "attribute";
    /**
     * The Constant REPEAT.
     */
    protected static final String REPEAT = "repeat";
    /**
     * The Constant RANDOM_STRING.
     */
    protected static final String RANDOM_STRING = "randomString";
    /**
     * The Constant LENGTH.
     */
    protected static final String LENGTH = "length";
    /**
     * The Constant TRY.
     */
    protected static final String TRY = "try";
    /**
     * The Constant ERROR_MESSAGE_NAME.
     */
    protected static final String ERROR_MESSAGE_NAME = "errorMessageName";
    /**
     * The Constant GENERATE_ERROR.
     */
    protected static final String GENERATE_ERROR = "generateError";
    /**
     * The Constant SEND_MAIL.
     */
    protected static final String SEND_MAIL = "sendMail";
    /**
     * The Constant SMTP_SERVER.
     */
    protected static final String SMTP_SERVER = "smtpServer";
    /**
     * The Constant SUBJECT.
     */
    protected static final String SUBJECT = "subject";
    /**
     * The Constant TO.
     */
    protected static final String TO = "to";
    /**
     * The Constant CC.
     */
    protected static final String CC = "cc";
    /**
     * The Constant BCC.
     */
    protected static final String BCC = "bcc";
    /**
     * The Constant SET_XML_VALUE.
     */
    protected static final String SET_XML_VALUE = "setXMLValue";
    /**
     * The Constant DB_CONNECT.
     */
    protected static final String DB_CONNECT = "dbConnect";
    /**
     * The Constant DB_CLOSE.
     */
    protected static final String DB_CLOSE = "dbClose";
    /**
     * The Constant DB_COMMIT.
     */
    protected static final String DB_COMMIT = "dbCommit";
    /**
     * The Constant DB_ROLLBACK.
     */
    protected static final String DB_ROLLBACK = "dbRollback";
    /**
     * The Constant DB_QUERY.
     */
    protected static final String DB_QUERY = "dbQuery";
    /**
     * The Constant DB_UPDATE.
     */
    protected static final String DB_UPDATE = "dbUpdate";
    /**
     * The Constant DB_GET_COLUMN_COUNT.
     */
    protected static final String DB_GET_COLUMN_COUNT = "dbGetColumnCount";
    /**
     * The Constant DB_GET_COLUMN_NAME.
     */
    protected static final String DB_GET_COLUMN_NAME = "dbGetColumnName";
    /**
     * The Constant DB_GET_COLUMN_INDEX.
     */
    protected static final String DB_GET_COLUMN_INDEX = "dbGetColumnIndex";
    /**
     * The Constant DB_GET_VALUE.
     */
    protected static final String DB_GET_VALUE = "dbGetValue";
    /**
     * The Constant DB_NEXT_ROW.
     */
    protected static final String DB_NEXT_ROW = "dbNextRow";
    /**
     * The Constant DRIVER.
     */
    protected static final String DRIVER = "driver";
    /**
     * The Constant AUTO_COMMIT.
     */
    protected static final String AUTO_COMMIT = "autoCommit";
    /**
     * The Constant RANDOM_INT.
     */
    protected static final String RANDOM_INT = "randomInt";
    /**
     * The Constant MAX_EXCLUSIVE.
     */
    protected static final String MAX_EXCLUSIVE = "maxExclusive";
    /**
     * The Constant CONTINUE.
     */
    protected static final String CONTINUE = "continue";
    /**
     * The Constant BREAK.
     */
    protected static final String BREAK = "break";
    /**
     * The Constant SOAP_CALL.
     */
    protected static final String SOAP_CALL = "soapCall";
    /**
     * The Constant SERVICE_NAME.
     */
    protected static final String SERVICE_NAME = "serviceName";
    /**
     * The Constant OPERATION.
     */
    protected static final String OPERATION = "operation";
    /**
     * The Constant SSL_CERTIFICATE_LOCATION.
     */
    protected static final String SSL_CERTIFICATE_LOCATION =
            "sslCertificateLocation";
    /**
     * The Constant XML_GET_RESPONSE.
     */
    protected static final String XML_GET_RESPONSE = "xmlGetResponse";
    /**
     * The Constant MASS_NAMESPACE.
     */
    protected static final String MASS_NAMESPACE = "http://www.esa.int/mass";
    /**
     * The Constant CLASS.
     */
    protected static final String CLASS = "class";
    /**
     * The Constant INTERNAL_CLASS.
     */
    protected static final String INTERNAL_CLASS = "internalClass";
    /**
     * The Constant TYPE.
     */
    protected static final String TYPE = "type";
    /**
     * The Constant DIMENSIONS.
     */
    protected static final String DIMENSIONS = "dimensions";
    /**
     * The Constant VALUE.
     */
    protected static final String VALUE = "value";
    /**
    
    /**
     * The Constant ARRAY.
     */
    protected static final String ARRAY = "array";
    /**
     * The Constant LOCATION.
     */
    protected static final String LOCATION = "location";
    /**
     * The Constant PARAMETER.
     */
    protected static final String PARAMETER = "parameter";
    /**
     * The Constant METHOD_NAME.
     */
    protected static final String METHOD_NAME = "methodName";
    /**
     * The Constant PUSH.
     */
    protected static final String PUSH = "push";
    /**
     * The Constant PULL.
     */
    protected static final String PULL = "pull";
    /**
     * The Constant COMMON_INPUT.
     */
    protected static final String COMMON_INPUT = "commonInput";
    /**
     * The Constant IS_SUBSCRIPTION.
     */
    protected static final String IS_SUBSCRIPTION = "isSubscription";
    /**
     * The Constant ZERO.
     */
    protected static final String ZERO = "0";
    /**
     * The Constant SERVICE_MODE.
     */
    protected static final String SERVICE_MODE = "serviceMode";
    /**
     * The Constant OPERATION_MODE.
     */
    protected static final String OPERATION_MODE = "operationMode";
    /**
     * The Constant RETURN.
     */
    protected static final String RETURN = "return";
    /**
     * The Constant RESULT.
     */
    protected static final String RESULT = "Result";
    /**
     * The Constant INPUT_MSG.
     */
    protected static final String INPUT_MSG = "InputMsg";
    /**
     * The Constant OUTPUT_MSG.
     */
    protected static final String OUTPUT_MSG = "OutputMsg";
    /**
     * The Constant GET.
     */
    protected static final String GET = "get";
    /**
     * The Constant CLEANUP_PROCEDURE.
     */
    protected static final String CLEANUP_PROCEDURE = "cleanupProcedure";
    /**
     * The Constant ADD_CLEANUP_MARKER.
     */
    protected static final String ADD_CLEANUP_MARKER = "addCleanupMarker";
    /**
     * The Constant REMOVE_CLEANUP_MARKER.
     */
    protected static final String REMOVE_CLEANUP_MARKER = "removeCleanupMarker";
    /**
     * The Constant CLEANUP_MARKERS.
     */
    protected static final String CLEANUP_MARKERS = "cleanupMarkers";
    /**
     * The Constant MARKER.
     */
    protected static final String MARKER = "marker";
    /**
     * The Constant DB_NUM_OF_ROWS.
     */
    protected static final String DB_NUM_OF_ROWS = "dbGetNumOfRows";
    /**
     * The Constant DB_SET_CURSOR.
     */
    protected static final String DB_SET_CURSOR = "dbSetCursor";
    /**
     * The Constant DB_EXECUTE.
     */
    protected static final String DB_EXECUTE = "dbExecute";
    /**
     * The Constant DB_EXECUTE_UPDATE.
     */
    protected static final String DB_EXECUTE_UPDATE = "dbExecuteUpdate";
    /**
     * The Constant PROCESS.
     */
    protected static final String PROCESS = "process";
    /**
     * The Constant FTP_RENAME.
     */
    protected static final String FTP_RENAME = "ftpRename";
    /**
     * The Constant JELLY.
     */
    protected static final String JELLY = "jelly";
    /**
     * The Constant THROW.
     */
    protected static final String THROW = "throw";
    /**
     * The Constant WRITE_PERMISSION.
     */
    protected static final String WRITE_PERMISSION = "writePermission";
    /**
     * The Constant ROOT.
     */
    protected static final String ROOT = "/";
    /**
     * The Constant SCOPE.
     */
    protected static final String SCOPE = "scope";
    /**
     * The Constant DELAY.
     */
    protected static final String DELAY = "delay";
    /**
     * The Constant DELAY.
     */
    protected static final String NEW_REMOTE_PATH = "newRemotePath";
    /**
     * The Constant APPLICATION.
     */
    protected static final String APPLICATION = "application";
    /**
     * The Constant RESOURCE_KEY.
     */
    protected static final String RESOURCE_KEY = "resourceKey";
    /**
     * The Constant ARRAY_DIMENSION_SYMBOL.
     */
    protected static final String ARRAY_DIMENSION_SYMBOL = "[";
    /**
     * The Constant SUB_CLASS_SYMBOL.
     */
    protected static final String SUB_CLASS_SYMBOL = "$";
}
