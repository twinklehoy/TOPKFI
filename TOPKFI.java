// This file is a template for the project of Dati e Algoritmi AA 2022-23

// You are free to modify this code, but do not use any library outside of
// java.io and java.util. Check carefully the input and output format,
// use the one described in the project specifications otherwise we may not
// be able to test and evaluate your submission!

// The following code reads the input dataset line by line, parsing the items in each transaction.
// Before the submission, disable any debug output (for example, setting the variable
// DEBUG to false) and only output the results as described in the project specifications.


import java.io.*;
import java.util.*;

public class TOPKFI{

    public static boolean DEBUG = false;

    public static QueueElem support_and_transactions(QueueElem elem, int item, ArrayList<Integer> transactions_a, ArrayList<Integer> transactions_b){
        Iterator<Integer> it = transactions_b.iterator();
        int size = transactions_a.size()>transactions_b.size()?transactions_b.size(): transactions_a.size();
        ArrayList<Integer> new_transactions = new ArrayList<>(size);
        int s = 0;
        if (transactions_a.size()>0 && transactions_b.size()>0){
            Iterator<Integer> ita = transactions_a.iterator();
            Iterator<Integer> itb = transactions_b.iterator();
            int a  = ita.next();
            int b = itb.next();
            while (true) {
                if (a == b) {
                    new_transactions.add(a);
                    if (ita.hasNext()) {
                        a = ita.next();
                    } else {
                        break;
                    }
                    if (itb.hasNext()) {
                        b = itb.next();
                    } else {
                        break;
                    }
                } else if (a < b) {
                    if (ita.hasNext()) {
                        a = ita.next();
                    } else {
                        break;
                    }
                } else {
                    if (itb.hasNext()) {
                        b = itb.next();
                    } else {
                        break;
                    }
                }
            }
        }
        s = new_transactions.size();
        return new QueueElem(s, item, elem, new_transactions);
    }

    public static void main(String args[]){

        // parse input arguments
        if(args.length != 3){
            System.out.println("The arguments are not correct!");
            System.out.println("Please use \njava TopKFI datasetpath K M");
            return;
        }

        String db_path = args[0];
        int K = Integer.parseInt(args[1]);
        int M = Integer.parseInt(args[2]);

        if(K < 0 || M < 0){
            System.out.println("K and M should be positive!");
            return;
        }

        if(DEBUG){
            System.out.println("Path to dataset: "+db_path);
            System.out.println("K: "+K);
            System.out.println("M: "+M);
        }

        HashSet<Integer> itemset = new HashSet(500, 1);
        HashMap<Integer, QueueElem> item_map = new HashMap<>(500,1);
        // read the input file
        try {
            File file_db = new File(db_path);
            Scanner db_reader = new Scanner(file_db);
            int transaction_id = 0;
            while (db_reader.hasNextLine()) {
                transaction_id++;
                String transaction = db_reader.nextLine();
                if(DEBUG){
                    System.out.println("transaction "+transaction_id+" is "+transaction);
                }
                String[] items_str = transaction.split("\\s+");
                int[] items = new int[items_str.length];
                /* read the transaction "items_str" into the array "items" */
                for(int i=0; i<items_str.length; i++){
                    try{
                        items[i] = Integer.parseInt(items_str[i]);
                        if(DEBUG){
                            System.out.println("  item "+items[i]);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Input format of transaction is wrong!");
                        System.out.println("transaction "+transaction_id+" is "+transaction);
                        e.printStackTrace();
                        return;
                    }
                }
                /* do something with the array "items" ... */
                for (int i=0; i<items.length; i++){
                    QueueElem elem;
                    if (!item_map.containsKey(items[i])){
                        elem = new QueueElem(0, items[i], null, new ArrayList<>());
                        item_map.put(items[i], elem);
                    }
                    elem = item_map.get(items[i]);
                    elem.p += 1;
                    elem.transactions.add(transaction_id);
                    itemset.add(items[i]);
                }
            }
            //System.out.println("MAX TID="+transaction_id);
            db_reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("The file "+db_path+" does not exist!");
            e.printStackTrace();
            return;
        }
        /* Initialize priority queue with single items... */
        PriorityQueue<QueueElem> Q = new PriorityQueue<>(itemset.size(), new QueueComparator());
        PriorityQueue<QueueElem> Q_tmp = null;
        Iterator<Integer> item_iterator = itemset.iterator();
        QueueElem elem;
        while (item_iterator.hasNext()){
            int item = item_iterator.next();
            elem = item_map.get(item);
            Q.add(elem);
        }
        // initialize S
        LinkedList<QueueElem> S = new LinkedList<>();
        elem = null;
        for (int k=0; k< K; k++){
            if (Q.isEmpty()){
                break;
            }
            elem = Q.poll();
            S.add(elem);
            int a = elem.item;

            int j = K-S.size();
            int simga_j = 0;

            LinkedList<QueueElem> firstjelems = new LinkedList<>();
            if (Q.size()>=j && j>=1){
                QueueElem e = null;
                for (int i=0; i<j;i++){
                    e = Q.poll();
                    firstjelems.add(e);
                }
                simga_j = e.p;
                Iterator<QueueElem> elem_iterator = firstjelems.iterator();
                while(elem_iterator.hasNext()){
                    Q.add(elem_iterator.next());
                }
            }

            if (Q.size()>2){
                Q_tmp = new PriorityQueue<>(Q.size(), new QueueComparator());
                while (Q.size()>0) {
                    QueueElem prova = Q.poll();
                    if (prova.p>=simga_j) Q_tmp.add(prova);
                    else{
                        prova.delete();
                        prova = null;

                    }
                }
                Q = null;
                Q = Q_tmp;
            }

            item_iterator = itemset.iterator();
            HashSet<Integer> new_itemset = new HashSet<>(500,1);
            while(item_iterator.hasNext()){
                int b = item_iterator.next();
                if (item_map.containsKey(b) && b>a && item_map.get(b).p>=simga_j){
                    QueueElem new_elem = support_and_transactions(elem, b, elem.transactions, item_map.get(b).transactions);
                    if (new_elem.p>0) {
                        Q.add(new_elem);
                    }
                }
                if (item_map.containsKey(b) && item_map.get(b).p>=simga_j) {
                    new_itemset.add(b);
                }
                if (item_map.containsKey(b) &&item_map.get(b).p<simga_j){
                    item_map.get(b).delete();
                    item_map.remove(b);
                }
            }
            itemset.clear();
            itemset = new_itemset;
            System.gc();
        }

        if (elem == null) return;

        int sigma_k = elem.p;
        while (!Q.isEmpty()){
            elem = Q.poll();
            if (elem.p != sigma_k) break;
            S.add(elem);
            int a = elem.item;

            item_iterator = itemset.iterator();
            while(item_iterator.hasNext()){
                int b = item_iterator.next();
                if ((b>a) && (item_map.get(b).p>=sigma_k)){
                    QueueElem new_elem = support_and_transactions(elem, b, elem.transactions, item_map.get(b).transactions);
                    if (new_elem.p>0) Q.add(new_elem);
                }
            }
        }

        System.out.println(S.size());
        if (S.size()<M){
            Iterator<QueueElem> elem_iterator = S.iterator();
            while(elem_iterator.hasNext()){
                System.out.println(elem_iterator.next());
            }
        }
    }
}
class QueueElem {
    public int p = 0;
    Integer item = null;
    QueueElem parent = null;
    public ArrayList<Integer> transactions = null;
    QueueElem(int p, int item, QueueElem parent, ArrayList<Integer> transactions){
        this.p = p;
        this.item = item;
        this.parent = parent;
        this.transactions = transactions;
    }

    public String toString(){
        ArrayList<Integer> l = new ArrayList<>();
        QueueElem parent = this.parent;
        String out = "";
        while(parent!=null){
            l.add(parent.item);
            parent = parent.parent;
        }
        l.add(this.item);
        Collections.sort(l);
        Iterator<Integer> it = l.iterator();
        while(it.hasNext()){
            out += it.next() + " ";
        }
        out+="("+this.p+")";
        return out;
    }
    public void delete(){
        if (this.transactions == null) return;
        this.parent = null;
        this.transactions.clear();
        this.transactions = null;
    }
}

class QueueComparator implements Comparator<QueueElem> {
    public int compare(QueueElem e1, QueueElem e2){
        if (e1.p < e2.p) return 1;
        else if (e1.p > e2.p) return -1;
        return 0;
    }
}
