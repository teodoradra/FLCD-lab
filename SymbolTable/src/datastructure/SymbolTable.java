package datastructure;

import java.util.ArrayList;

public class SymbolTable {

    ArrayList<HashNode<Integer, String>> symbolTable;
    int numBuckets=10;
    int size;

    public SymbolTable()
    {
        symbolTable = new ArrayList<>();
        for(int i = 0; i<numBuckets; i++)
        {
            symbolTable.add(null);
        }
    }

    public int getSize()
    {
        return size;
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    private int getSymbolTableIndex(String value)
    {
        int hashCod = value.hashCode();
        System.out.println(hashCod);
        return hashCod % numBuckets;
    }

    public int get(String value)
    {
        int index = getSymbolTableIndex(value);
        HashNode<Integer, String> head = symbolTable.get(index);
        while(head != null)
        {
            if(head.value.equals(value))
            {
                return head.key;
            }
            head=head.next;
        }
        return -1;
    }


    public void add(String value)
    {
        int index = getSymbolTableIndex(value);
        //System.out.println("The index of the key: " + value.toString() + " is : " + index);

        HashNode<Integer, String> head = symbolTable.get(index);   // if there is a value on that index
        HashNode<Integer, String> toAdd = new HashNode<>();
        toAdd.key = index;
        toAdd.value = value;
        if(head == null)    // if there is not this index in the table already
        {
            symbolTable.set(index, toAdd);
            size++;
        }
        else
        {
            while(head != null)
            {
                if(head.value.equals(value))
                {
                    break;
                }
                head = head.next;
            }
            if(head == null)
            {
                head = symbolTable.get(index);
                toAdd.next = head;
                symbolTable.set(index, toAdd);
                size++;
            }
        }
        if ((1.0 * size) / numBuckets > 0.8)
        {
            //do something
            ArrayList<HashNode<Integer, String>> tmp = symbolTable;
            symbolTable=new ArrayList<>();
            numBuckets = 2*numBuckets;
            for(int i=0; i<numBuckets; i++)
            {
                symbolTable.add(null);
            }
            for(HashNode<Integer, String> headNode: tmp)
            {
                while(headNode!=null)
                {
                    add(headNode.value);
                    headNode = headNode.next;
                }
            }
        }
    }

    public void tostring(){
        HashNode<Integer, String> headNode;
        for (int i = 0; i< this.numBuckets; i++){
            System.out.println("---" + i + "---");
            headNode = symbolTable.get(i);
            while(headNode != null)
            {
                System.out.println(headNode.key + " - " + headNode.value);
                headNode = headNode.next;
            }
        }
    }

    public static void main(String[] args)
    {
        SymbolTable map = new SymbolTable();
        map.add("this");
        map.add("ths");
        map.add("thi");
        map.add("uhi");
        map.add("phi");
        map.add("ghi");
        map.add("hhi");
        map.add("ohi");
        map.add("ohi");
        map.add("ith");
        System.out.println(map.get("ohi"));
        map.tostring();

    }

}
