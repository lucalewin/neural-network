package me.lucraft.utils.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

//Downloaded
public class Parser {
	

	
	private Node mainContent;
	private String fileName;
	
	public void load(String file) throws Exception {
		this.fileName = file;
		String data = "<MainContent>";
		BufferedReader reader = new BufferedReader(new FileReader (file));
		String line;
		while((line = reader.readLine())!= null)
			if(!line.trim().equals(""))
				data += line;
		data += "</MainContent>";
		this.generateNodes(data);
		reader.close();
	}
	
	public void create(String fileName) {
		this.fileName = fileName;
		this.mainContent = new Node("MainContent");
	}
	
	private void generateNodes(String data) throws Exception {
		mainContent = Node.parse(data);
	}
	
	public Node getContent() {
		return mainContent;
	}

	public String getValue(String[] keys, String attr) {
		Node current = mainContent;
		for(String k : keys) {
			current = current.getChild(k);
			if(current == null) return null;
		}
		return current.getAttribute(attr).getValue();
	}
	
	public void setValue(String[] keys, String attr, String value) {
		Node current = mainContent;
		for(String k : keys) {
			current = current.getChild(k);
			if(current == null) return;
		}
		if(current.containsAttribute(new Attribute(attr, "")))
			current.setAttribute(attr, value);
		else
			current.addAttribute(attr, value);
		
	}
	
	public void addNode(String[] keys, Node n) {
		Node current = mainContent;
		for(String k : keys) {
			current = current.getChild(k);
			if(current == null) return;
		}
		current.addChild(n);
	}
	
	public void addNode(String[] keys, String node) {
		Node curr = mainContent;
		for(String k : keys){
			curr = curr.getChild(k);
			if(curr == null) return;
		}
		curr.addChild(new Node(node));
	}
	
	public void close() throws Exception {
		PrintWriter out = new PrintWriter(fileName);
		for(Node node : mainContent.getChilds())
			out.print(node.toParse(0));
		out.close();
	}
	
	
}

