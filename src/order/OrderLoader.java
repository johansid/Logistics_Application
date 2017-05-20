package order;

import order.Order;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OrderLoader {

public static ArrayList<Order> load() {
        
        ArrayList<Order> orders = new ArrayList<Order>();
        try {
            String fileName = "Orders.xml";

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            File orderXml = new File(fileName);
            if (!orderXml.exists()) {
                System.err.println("**** XML File '" + fileName + "' cannot be found");
                System.exit(-1);
            }
            
            Document doc = db.parse(orderXml);
            doc.getDocumentElement().normalize();

            NodeList orderEntries = doc.getDocumentElement().getChildNodes();
            
            //create ArrayList storing facilities

            for (int i = 0; i < orderEntries.getLength(); i++) {
                if (orderEntries.item(i).getNodeType() == Node.TEXT_NODE) {
                    continue;
                }
                String entryName = orderEntries.item(i).getNodeName();
                if (!entryName.equals("Order")) {
                    System.err.println("Unexpected node found: " + entryName);
                    continue;
                }
                
                // Get a node attribute
                NamedNodeMap orderMap = orderEntries.item(i).getAttributes();
                
                // Get information of a node 
                //String fcltId = orderMap.getNamedItem("Id").getNodeValue();
                
                Element order = (Element) orderEntries.item(i);
                String orderId = order.getElementsByTagName("Id").item(0).getTextContent();
                String orderTimeS = order.getElementsByTagName("OrderTime").item(0).getTextContent();
                String orderDest = order.getElementsByTagName("Destination").item(0).getTextContent();
                int orderTime = Integer.parseInt(orderTimeS);

                // Store pairs of <Name, Distance> in neighbors, <Id, Quantity> in inventories
                Map <String, Integer> items = new HashMap<String, Integer>(); 
                
                NodeList itemList = order.getElementsByTagName("Item");
                
                // Get Neighbors - there can be 0 or more
                for (int j = 0; j < itemList.getLength(); j++) {
                    if (itemList.item(j).getNodeType() == Node.TEXT_NODE) {
                        continue;
                    }
                    entryName = itemList.item(j).getNodeName();
                    if (!entryName.equals("Item")) {
                        System.err.println("Unexpected node found: " + entryName);
                        continue;
                    }

                    // Get neighbors' information
                    order = (Element) itemList.item(j);
                    String itemName = order.getElementsByTagName("Name").item(0).getTextContent();
                    String itemQtyS = order.getElementsByTagName("Qty").item(0).getTextContent();               
                    int itemQty = Integer.parseInt(itemQtyS);
                    // Put into Hashmap
                    items.put(itemName, itemQty);
                }
                        

                Order orderobj = OrderImplFactory.createOrder(orderId, 
                                    orderTime, orderDest, items);
                //fclts.put(fcltName,facility);
                orders.add(orderobj);
            }

        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            e.printStackTrace();
        }
        
        return orders;
    }
}