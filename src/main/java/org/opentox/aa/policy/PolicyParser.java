package org.opentox.aa.policy;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parses OpenSSO XML policies.  Should be moved to opentox-opensso library
 * <pre>http://java.net/projects/opensso/</pre>
 * @author nina

 */
public abstract class PolicyParser<USER,GROUP,POLICYRULE,POLICY> {
	protected Document doc;
	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
	public enum type {
		cn {
			@Override
			public String toString() {
				return "User";
			}
		},
		member {
			@Override
			public String toString() {
				return "members";
			}
		},
		LDAPUsers {
			@Override
			public String toString() {
				return "Applies to user ";
			}
		},
		LDAPGroups {
			@Override
			public String toString() {
				return "Applies to group ";
			}
		};
	}
	public enum tags {
		ResourceName,
		Policies,
		Policy,
		Rule,
		Subjects,
		Subject,
		AttributeValuePair,
		Attribute,
		Value
		
	}
	public PolicyParser(String content) throws Exception {
		doc = parse(content);
	}
	
	public Document parse(String content) throws IOException,  SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
       
        StringReader reader = new StringReader(content);
        Document doc = builder.parse(new InputSource(reader));
        doc.normalize();
        return doc;
	}
	
	public POLICY getAccessRights() throws Exception {
		POLICY accessRights = createEmptyPolicy();
		
		Element top = doc.getDocumentElement();
		org.w3c.dom.NodeList policies = top.getElementsByTagName(tags.Policy.toString());
		for (int i=0; i < policies.getLength(); i++) {
			Element policy = (Element) policies.item(i);
			setPolicyID(accessRights, policy.getAttribute("name"));
			//subjects
			USER user  = null;
			GROUP group = null;
			org.w3c.dom.NodeList subjects = policy.getElementsByTagName(tags.Subjects.toString());
			for (int j=0; j < subjects.getLength(); j++) {
				Element subject = ((Element) subjects.item(j));
				org.w3c.dom.NodeList ss = subject.getElementsByTagName(tags.Subject.toString());
				for (int l=0; l < ss.getLength(); l++) {
					Element s = ((Element) ss.item(j));
					String name = s.getAttribute("name");
					if (name==null) continue;
					
					type t = null;
					try {
						t = type.valueOf(s.getAttribute("type"));
						switch (t) {
						case LDAPGroups: {
							group = createGroup(name);
							break;
						}
						case LDAPUsers: {
							user = createUser(name);
							break;
						}
						}
					} catch (Exception x) {
						
					}
					
					/*
					b.append(String.format("&nbsp;&nbsp;&nbsp;%s <b>%s</b> [%s]<br>\n",
							t,
							name,
							s.getAttribute("includeType")));
					*/
				}

			}
			//rules
			org.w3c.dom.NodeList rules = policy.getElementsByTagName(tags.Rule.toString());
			for (int j=0; j < rules.getLength(); j++) {
				Element rule = ((Element) rules.item(j));
				
				POLICYRULE policyRule = createPolicyRule(rule.getAttribute("name"),user,group);
				addPolicyRule(accessRights, policyRule);
				org.w3c.dom.NodeList ResourceName = rule.getElementsByTagName(tags.ResourceName.toString());
				for (int k=0; k < ResourceName.getLength(); k++) {
					Element attr = ((Element) ResourceName.item(k));
					setResource(accessRights, new URL(attr.getAttribute("name")));
				}	
				
				org.w3c.dom.NodeList attrs = rule.getElementsByTagName(tags.AttributeValuePair.toString());
				for (int k=0; k < attrs.getLength(); k++) {
					processAttrValuePair(((Element) attrs.item(k)),policyRule);
				}

			}
			
		}
		return accessRights;
	}
	
	public void processAttrValuePair(Element vp, POLICYRULE policyRule) {
		org.w3c.dom.NodeList attr = vp.getElementsByTagName(tags.Attribute.toString());
		Method method = null;
		for (int k=0; k < attr.getLength(); k++) try {
			method = Method.valueOf(((Element)attr.item(k)).getAttribute("name"));
		} catch (Exception x) {method = null;}
		if (method==null) return;
		org.w3c.dom.NodeList val = vp.getElementsByTagName(tags.Value.toString());
		for (int k=0; k < val.getLength(); k++) {
			String v = ((Element)val.item(k)).getTextContent();
			setPolicyRuleMethod(policyRule, method, "allow".equals(v));
		}	
	}	
	protected abstract POLICY createEmptyPolicy();
	protected abstract void addPolicyRule(POLICY policy, POLICYRULE policyRule);
	protected abstract void setPolicyID(POLICY policy,String policyId);
	protected abstract void setResource(POLICY policy,URL url);
	protected abstract USER createUser(String username);
	protected abstract GROUP createGroup(String groupname);
	protected abstract void setPolicyRuleMethod(POLICYRULE policyRule, Method method, Boolean value);
	protected abstract POLICYRULE createPolicyRule(String name,USER user,GROUP group);
}
