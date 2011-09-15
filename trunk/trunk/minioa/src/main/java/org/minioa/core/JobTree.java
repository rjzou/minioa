package org.minioa.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

public class JobTree {
	public MySession mySession;

	public MySession getMySession() {
		if (mySession == null)
			mySession = (MySession) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("MySession");
		return mySession;
	}
	
	private TreeNode rootNode = null;
	private List<String> selectedNodeChildren = new ArrayList<String>();

	private String nodeTitle;
	private static final String DATA_PATH = FunctionLib.getBaseDir() + "job.properties";

	private void addNodes(String path, TreeNode node, Properties properties) {
		boolean end = false;
		int counter = 1;
		while (!end) {
			String key = path != null ? path + '.' + counter : String.valueOf(counter);
			String value = properties.getProperty(key);
			if (value != null) {
				String[] arr = value.split("\\,");
				TreeNodeImpl nodeImpl = new TreeNodeImpl();
				nodeImpl.setData(new Job(arr[0],Integer.valueOf(arr[1]), arr[2]));
				node.addChild(new Integer(counter), nodeImpl);
				addNodes(key, nodeImpl, properties);
				counter++;
			} else {
				end = true;
			}
		}
	}

	private void loadTree() {
		try {
			File f = new File(DATA_PATH);
			if (f.exists()) {
				InputStream is = new FileInputStream(DATA_PATH);
				Properties property = new Properties();
				property.load(is);
				rootNode = new TreeNodeImpl();
				addNodes(null, rootNode, property);
			}
		} catch (IOException e) {
			throw new FacesException(e.getMessage(), e);
		}
	}

	public void processSelection(NodeSelectedEvent event) {
		HtmlTree tree = (HtmlTree) event.getComponent();
		Job bean = (Job) tree.getRowData();
		if(bean.getType().equals("job")){
			getMySession().getTempInt().put("Job.id", bean.getID_());
			getMySession().getTempStr().put("Job.jobName", bean.getJobName());
		}else{
			getMySession().getTempInt().put("Job.id", 0);
			getMySession().getTempStr().put("Job.jobName", "");
		}
	}
	public void processSelection2(NodeSelectedEvent event) {
		HtmlTree tree = (HtmlTree) event.getComponent();
		Job bean = (Job) tree.getRowData();
		if(bean.getType().equals("job")){
			getMySession().getTempInt().put("Job.id2", bean.getID_());
			getMySession().getTempStr().put("Job.jobName2", bean.getJobName());
		}else{
			getMySession().getTempInt().put("Job.id2", 0);
			getMySession().getTempStr().put("Job.jobName2", "");
		}
		System.out.println(getMySession().getTempInt().get("Job.id2"));
	}
	public TreeNode getTreeNode() {
		if (rootNode == null) {
			loadTree();
		}
		return rootNode;
	}

	public String getNodeTitle() {
		return nodeTitle;
	}

	public void setNodeTitle(String nodeTitle) {
		this.nodeTitle = nodeTitle;
	}
}