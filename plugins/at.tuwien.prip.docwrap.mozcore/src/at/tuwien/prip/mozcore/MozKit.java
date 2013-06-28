package at.tuwien.prip.mozcore;

import static at.tuwien.prip.mozcore.utils.ProxyUtils.qi;

import java.io.File;
import java.util.LinkedList;
import java.util.Stack;

import org.mozilla.interfaces.nsIDOM3Node;
import org.mozilla.interfaces.nsIDOMAttr;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.interfaces.nsIWebBrowserSetup;
import org.mozilla.interfaces.nsIWebNavigation;
import org.mozilla.xpcom.Mozilla;


public class MozKit {

    private static boolean initialized = false;

    /**
     * 
     * Intialize.
     * 
     * @param moz
     * @param grePath
     */
    protected static void initialize(Mozilla moz, File grePath)  {
        if (initialized) return;
        
        
//        moz.setInterfaceProvider(new DefaultInterfaceProvider("org.weblearn.idl"));
//        createProxied("@weblearn.org/domutils;1", wlIDOMUtils.class);
//        WebLearnNative nat = WebLearnNative.getInstance();
//        nat.init(grePath);

//        registerDemoProtocolHandler();

        initialized = true;
    }

    public static void destroy () {
        initialized = false;
    }

    public static void disableImages(nsIWebBrowser brow) {
        nsIWebBrowserSetup bset =
            (nsIWebBrowserSetup) brow.queryInterface(nsIWebBrowserSetup.NS_IWEBBROWSERSETUP_IID);
        bset.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_PLUGINS, 0);
        bset.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_IMAGES, 0);
    }

    public static void enableImages(nsIWebBrowser brow) {
        nsIWebBrowserSetup bset =
            (nsIWebBrowserSetup) brow.queryInterface(nsIWebBrowserSetup.NS_IWEBBROWSERSETUP_IID);
        bset.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_PLUGINS, 1);
        bset.setProperty(nsIWebBrowserSetup.SETUP_ALLOW_IMAGES, 1);
    }

    public static void stopLoading(nsIWebBrowser brow) {
        nsIWebNavigation nav =
            (nsIWebNavigation)
            brow.queryInterface(nsIWebNavigation.NS_IWEBNAVIGATION_IID);
        nav.stop(nsIWebNavigation.STOP_ALL);
    }

    /**
     * 
     * 
     *
     */
    public static class XPath {

        public static String getExactXPath(nsIDOMNode n) {
            return getExactXPath(n, true, false);
        }

        public static String getExactXPath(nsIDOMNode n,
                                           boolean addIndices,
                                           boolean addAttributes)
        {
            return getRelativeXPath(null, n, addIndices, addAttributes);
        }


        public static String getIndirectExactXPath(nsIDOMNode context, nsIDOMNode target)
        {
            nsIDOMNode common = getClosestCommonAncestor(context, target);
            String xpup = getRelativeUpwardXPath(common, context, true, false);
            String xpdown = getRelativeExactXPath(common, target);

            final String xp;
            if (xpdown.equals("."))
                xp = xpup;
            else if (xpdown.startsWith("./"))
                xp = xpup + xpdown.substring(1);
            else
                xp = xpup + "/" + xpdown;

            return xp;
        }

        public static String
            getRelativeUpwardXPath(nsIDOMNode ancestor,
                                   nsIDOMNode n,
                                   boolean addNodeNames,
                                   boolean addAttributes)
        {
            LinkedList<String> names = new LinkedList<String>();
            LinkedList<String> attribs = new LinkedList<String>();

            boolean reachedAncestor = false;
            while (n!=null && n.getNodeType()==nsIDOMNode.ELEMENT_NODE) {

                String name = n.getNodeName();
                //ErrorDump.debug(this, "name="+name);
                names.add(name);

                if (addAttributes) {
                    attribs.add(getAttributeXPath(n));
                }

                nsIDOM3Node n3 = qi(n, nsIDOM3Node.class);
                if (ancestor!=null && n3.isSameNode(ancestor)) {
                    reachedAncestor = true;
                    break;
                }

                //move to parent
                n = n.getParentNode();
            }

            if (ancestor!=null && !reachedAncestor)
                return null;

            StringBuffer sb = new StringBuffer();
            boolean first = true;
            while (!names.isEmpty()) {
                String name = names.removeFirst();
                if (first) {
                    sb.append(".");
                    first = false;
                } else {
                    sb.append("/parent::");
                    if (addNodeNames) sb.append(name);
                    else sb.append('*');
                }

                if (addAttributes) {
                    String a = attribs.removeFirst();
                    if (a.length()>0) sb.append("["+a+"]");
                }
            }

            return sb.toString();
        }

        public static String getRelativeExactXPath(nsIDOMNode ancestor, nsIDOMNode descendant)
        {
            return getRelativeXPath(ancestor, descendant, true, false);
        }

        public static String getRelativeXPath(nsIDOMNode context,
                                              nsIDOMNode n,
                                              boolean addIndices,
                                              boolean addAttributes)
        {
            Stack<String> names = new Stack<String>();
            Stack<Integer> indices = new Stack<Integer>();
            Stack<String> attribs = new Stack<String>();

            boolean reachedContext = false;
            while (n!=null && n.getNodeType()==nsIDOMNode.ELEMENT_NODE) {

                nsIDOM3Node n3 = qi(n, nsIDOM3Node.class);
                if (context!=null && n3.isSameNode(context)) {
                    reachedContext = true;
                    break;
                }

                String name = n.getNodeName();
                //ErrorDump.debug(this, "name="+name);
                names.push(name);

                if (addIndices) {
                    int idx = 1;
                    nsIDOMNode x = n.getPreviousSibling();
                    while (x!=null) {
                        //ErrorDump.debug(this, "    prev="+x.getNodeName()+" "+x.getNodeType());
                        if (x.getNodeType()==nsIDOMNode.ELEMENT_NODE &&
                            x.getNodeName().equalsIgnoreCase(name))
                        {
                            idx++;
                        }
                        x = x.getPreviousSibling();
                    }
                    //ErrorDump.debug(this, "idx="+idx);
                    indices.push(idx);
                }

                if (addAttributes) {
                    attribs.push(getAttributeXPath(n));
                }

                //move to parent
                n = n.getParentNode();
            }

            if (context!=null && !reachedContext)
                return null;

            StringBuffer sb = new StringBuffer();
            if (context!=null) sb.append(".");
            while (!names.isEmpty()) {
                //if (!name.equalsIgnoreCase("#document")) {
                String name = names.pop();
                sb.append('/'+name);
                //}
                if (addIndices) {
                    int index = indices.pop();
                    sb.append("["+index+"]");
                }
                if (addAttributes) {
                    String a = attribs.pop();
                    if (a.length()>0) sb.append("["+a+"]");
                }
            }

            return sb.toString();
        }

        private static String getAttributeXPath(nsIDOMNode n) {
            StringBuffer asb = new StringBuffer();
            nsIDOMNamedNodeMap as = n.getAttributes();
            for (int i=0; i<as.getLength(); i++) {
                nsIDOMAttr a = (nsIDOMAttr) as.item(i);
                String sa = getSingleAttributeXPath(a);
                if (asb.length()>0) asb.append(" and ");
                asb.append(sa);
            }
            return asb.toString();
        }

        private static String getSingleAttributeXPath(nsIDOMAttr a) {
            String sa =
                String.format("@%s=\"%s\"",
                              a.getName(),
                              a.getValue());
            return sa;
        }

        /**
         * 
         * Gets the the xpath for all kind of nodes and not just elements
         * 
         * @param n Attributes, text, comment, processing instruction, element
         * @return exact xpath to this node without attributes on the way
         */
        public static String getExactXPathFromNode(nsIDOMNode n) {
            String suffix = "";
            nsIDOMNode parent = n;
            int t = n.getNodeType();
            if (t!=nsIDOMNode.ELEMENT_NODE) { //in case that n is not an element we have to append the necessary xpath
                parent = n.getParentNode();  //needed for the attribute
                if (t==nsIDOMNode.ATTRIBUTE_NODE) {
                    nsIDOMAttr a = qi(n, nsIDOMAttr.class);
                    parent = a.getOwnerElement();
                    suffix = "["+getSingleAttributeXPath(a)+"]";
                } else if (t==nsIDOMNode.TEXT_NODE){
                    int index = getChildNodeTypeIndex(n, nsIDOMNode.TEXT_NODE);
                    if (index>=0) { //found an index
                        suffix="/text()["+index+"]";
                    }
                } else if (t==nsIDOMNode.COMMENT_NODE) {
                    int index = getChildNodeTypeIndex(n, nsIDOMNode.COMMENT_NODE);
                    if (index>=0) { //found an index
                        suffix="/comment()["+index+"]";
                    }
                } else if (t==nsIDOMNode.PROCESSING_INSTRUCTION_NODE) {
                    int index = getChildNodeTypeIndex(n, nsIDOMNode.PROCESSING_INSTRUCTION_NODE);
                    if (index>=0) { //found an index
                        suffix="/processing-instruction()["+index+"]";
                    }
                } else if (t==nsIDOMNode.DOCUMENT_NODE) {
                    return "/";
                }
            }
            String xpath = getExactXPath(parent);
            return xpath+suffix;
        }

        private static int getChildNodeTypeIndex(nsIDOMNode node, int type)
        {
            if (node==null)
                throw new NullPointerException("[DomHelper:getChildNodeIndex] Node cannot be null.");

            nsIDOMNodeList nl = qi(node.getParentNode(), nsIDOMElement.class).getChildNodes();
            int num_of_elems = 0;
            for (int i=0; i<nl.getLength(); i++) {
                nsIDOMNode n = nl.item(i);
                if (n.getNodeType()==type) {
                    num_of_elems++;
                    if (node==n) {
                        return num_of_elems;
                    }
                }
            }
            return -1;
        }

        /**
         * 
         * 
         * 
         * @param sn1
         * @param sn2
         * @return
         */
        public static nsIDOMNode getClosestCommonAncestor(nsIDOMNode sn1, nsIDOMNode sn2)
        {
            nsIDOM3Node sn13 = qi(sn1, nsIDOM3Node.class);
            if (sn13.isSameNode(sn2)) return sn1;

            Stack<nsIDOMNode> l1 = new Stack<nsIDOMNode>();
            nsIDOMNode n = sn1;
            while (n!=null) {
                l1.push(n);
                n = n.getParentNode();
            }

            Stack<nsIDOMNode> l2 = new Stack<nsIDOMNode>();
            n = sn2;
            while (n!=null) {
                l2.push(n);
                n = n.getParentNode();
            }

            //find the common ancestor node
            nsIDOMNode closure = null;
            while (!l1.isEmpty() && !l2.isEmpty()) {
                nsIDOMNode n1 = l1.pop();
                nsIDOM3Node n13 = qi(n1, nsIDOM3Node.class);
                nsIDOMNode n2 = l2.pop();

                if (n13.isSameNode(n2)) {
                    closure = n1;
                }
                else {
                    break;
                }
            }

            return closure;
        }
    }

//  public static void saveDocument(nsIDOMDocument doc, File toDir)
//  throws IOException
//{
////  if (!MozDOMHelper.isFromMozillaDOM(mozDoc)) throw new IOException("not a mozilla DOM");
//
////  nsIDOMDocument doc =
////      (nsIDOMDocument)
////      ((NodeImpl) mozDoc).getInstance().
////      queryInterface(nsIDOMDocument.NS_IDOMDOCUMENT_IID);
//
//  nsIComponentManager cm = Mozilla.getInstance().getComponentManager();
//  nsIWebBrowserPersist persist = (nsIWebBrowserPersist)
//    cm.createInstanceByContractID("@mozilla.org/embedding/browser/nsWebBrowserPersist;1",
//                                  null,
//                                   nsIWebBrowserPersist.NS_IWEBBROWSERPERSIST_IID);
//
//  //create target file (for html) and subdir (for images)
//  nsILocalFile file = (nsILocalFile)
//    cm.createInstanceByContractID("@mozilla.org/file/local;1",
//                                  null,
//                                   nsILocalFile.NS_ILOCALFILE_IID);
//  file.initWithPath(toDir.getAbsolutePath());
//  file.append("index.html");
//
//  nsILocalFile subdir = (nsILocalFile)
//    cm.createInstanceByContractID("@mozilla.org/file/local;1",
//                                  null,
//                                   nsILocalFile.NS_ILOCALFILE_IID);
//  subdir.initWithPath(toDir.getAbsolutePath());
//  subdir.append("res");
//
//  //start saving
//  long flags =
//      nsIWebBrowserPersist.PERSIST_FLAGS_NO_CONVERSION |
//      nsIWebBrowserPersist.PERSIST_FLAGS_REPLACE_EXISTING_FILES |
//      //don't download if data is already in cache
//      nsIWebBrowserPersist.PERSIST_FLAGS_FROM_CACHE;
//      //nsIWebBrowserPersist::PERSIST_FLAGS_BYPASS_CACHE
//      //nsIWebBrowserPersist::PERSIST_FLAGS_CLEANUP_ON_FAILURE
//  persist.saveDocument(doc, file, subdir, null, flags, 0);
//
//  //wait until the saving finishes, but
//  //spin the event loop, to run the saver
//
//  while (persist.getCurrentState()!=nsIWebBrowserPersist.PERSIST_STATE_FINISHED) {
//      MozKit.runMozillaThread(100);
//  }
//
//  long code = persist.getResult();
//  if (code!=Mozilla.NS_OK) {
//      throw new IOException(String.format("Document saving failed (%d)", code));
//  }
//
//}

//@SuppressWarnings("unchecked")
//public static byte[] saveURI(String uriStr) {
//  if (uriStr==null) return null;
//  nsIComponentManager cm = Mozilla.getInstance().getComponentManager();
//  nsIWebBrowserPersist persist =
//      (nsIWebBrowserPersist)
//      cm.createInstanceByContractID("@mozilla.org/embedding/browser/nsWebBrowserPersist;1",
//                                    null,
//                                    nsIWebBrowserPersist.NS_IWEBBROWSERPERSIST_IID);
//
//
//  nsIServiceManager sm = Mozilla.getInstance().getServiceManager();
//  nsIIOService ios =
//      (nsIIOService)
//      sm.getServiceByContractID("@mozilla.org/network/io-service;1",
//                                nsIIOService.NS_IIOSERVICE_IID);
//  nsIURI uri = ios.newURI(uriStr, null, null);
//
//
//  //create temporary file
//  GetPropertyAction a = new GetPropertyAction("java.io.tmpdir");
//  String dirPath = ((String) AccessController.doPrivileged(a));
//  File tmpDir = new File(dirPath);
//  File jfile;
//  try {
//      jfile = File.createTempFile("wrap","bin", tmpDir);
//  }
//  catch (IOException e) {
//      return null;
//  }
//  nsILocalFile file = (nsILocalFile)
//  cm.createInstanceByContractID("@mozilla.org/file/local;1",
//                                null,
//                                 nsILocalFile.NS_ILOCALFILE_IID);
//  file.initWithPath(jfile.getAbsolutePath());
//
//  //serialize the uri
//  persist.saveURI(uri, null, null, null, null, file);
//
//  while (persist.getCurrentState()!=
//         nsIWebBrowserPersist.PERSIST_STATE_FINISHED) {
//      Kit.runMozillaThread(100);
//  }
//  if (persist.getResult()!=Mozilla.NS_OK) return null;
//
//  //read the file into mem
//  try {
//      ByteArrayOutputStream os = new ByteArrayOutputStream();
//      InputStream is = new BufferedInputStream(new FileInputStream(jfile));
//      int n;
//      byte[] buf = new byte[1000];
//      while ((n=is.read(buf))!=-1) {
//          os.write(buf, 0, n);
//      }
//      is.close();
//      jfile.delete();
//      return os.toByteArray();
//  }
//  catch (IOException e) {
//      return null;
//  }
//}

//public static void sleep(int ms) {
//  try {
//      Thread.sleep(ms);
//  }
//  catch (InterruptedException e) {
//      ErrorDump.error(Kit.class, e);
//  }
//}

//public static Document getDocument(final nsIDOMDocument nsdoc) {
//  return
//  (Document)
//  NodeFactory.getNodeInstance(nsdoc);
//}

//public static void loadURI(final KitGecko gecko, final String uri) {
//  ErrorDump.debug(Kit.class, "Loading document");
//  guiSyncExec(new Runnable() {
//      public void run() {
//          gecko.setUrl(uri);
//      }
//  });
//  /*
//  long ptr = gecko.getnsIWebBrowserPointer();
//  nsIWebBrowser brow =
//      (nsIWebBrowser)
//      XPCOM.importInstance(ptr, nsIWebBrowser.NS_IWEBBROWSER_IID);
//  nsIWebNavigation nav =
//      (nsIWebNavigation)
//      brow.queryInterface(nsIWebNavigation.NS_IWEBNAVIGATION_IID);
//  nav.stop(nsIWebNavigation.STOP_ALL);
//  nsIDocShell dshell =
//      (nsIDocShell)
//      brow.queryInterface(nsIDocShell.NS_IDOCSHELL_IID);
//  nsIDocShellLoadInfo[] infoa = new nsIDocShellLoadInfo[] {};
//  dshell.createLoadInfo(infoa);
//  nsIDocShellLoadInfo linfo = infoa[0];
//
//  nsIServiceManager sm = XPCOM.getServiceManager();
//  nsIIOService ios =
//      (nsIIOService)
//      sm.getServiceByContractID("@mozilla.org/network/io-service;1",
//                                nsIIOService.NS_IIOSERVICE_IID);
//  nsIURI nsUri = ios.newURI(uri, null, null);
//
//  dshell.loadURI(nsUri,linfo,nsIWebNavigation.LOAD_FLAGS_NONE,true);
//  */
//
//  gecko.waitForDocLoad();
//  Kit.stopLoading(gecko);
//}

/*
long p = Wrap.gecko.getnsIWebBrowserPointer();
nsIWebBrowser brow =
  (nsIWebBrowser)
  XPCOM.importInstance(p, nsIWebBrowser.NS_IWEBBROWSER_IID);
brow.addWebBrowserListener(new Aaa(),
  nsIWebProgressListener.NS_IWEBPROGRESSLISTENER_IID);

private static class Aaa
  implements nsIWebProgressListener, nsIWeakReference
{
  public void onLocationChange(nsIWebProgress arg0, nsIRequest arg1, nsIURI arg2) {}
  public void onProgressChange(nsIWebProgress arg0, nsIRequest arg1, int arg2, int arg3, int arg4, int arg5) {};
  public void onSecurityChange(nsIWebProgress arg0, nsIRequest arg1, long arg2) {}
  public void onStateChange(nsIWebProgress arg0, nsIRequest arg1, long state, long staus) {

  }
  public void onStatusChange(nsIWebProgress arg0, nsIRequest arg1, long arg2, String arg3) {}
  public nsISupports queryInterface(String iid) {
      return XPCOM.queryInterface(this,iid);
  }

  public nsISupports queryReferent(String iid) {
      return queryInterface(iid);
  }
}
*/

}//MozKit
