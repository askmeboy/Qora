package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;
import qora.account.Account;
import qora.transaction.Transaction;
import database.DBMap;

public class TransactionsOfAddressMap extends DBMap<Tuple2<String, String>, byte[]> 
{
	private Map<Integer, Integer> observableData = new HashMap<Integer, Integer>();
	
	public TransactionsOfAddressMap(DBSet databaseSet, DB database)
	{
		super(databaseSet, database);
	}

	public TransactionsOfAddressMap(TransactionsOfAddressMap parent) 
	{
		super(parent);
	}
	
	protected void createIndexes(DB database){}

	@Override
	protected Map<Tuple2<String, String>, byte[]> getMap(DB database) 
	{
		//OPEN MAP
		return database.createTreeMap("transactionofaddress")
				.keySerializer(BTreeKeySerializer.TUPLE2)
				.makeOrGet();
	}

	@Override
	protected Map<Tuple2<String, String>, byte[]> getMemoryMap() 
	{
		return new TreeMap<Tuple2<String, String>, byte[]>(Fun.TUPLE2_COMPARATOR);
	}

	@Override
	protected byte[] getDefaultValue() 
	{
		return new byte[0];
	}
	
	@Override
	protected Map<Integer, Integer> getObservableData() 
	{
		return this.observableData;
	}

	public boolean add(Account account, Transaction transaction)
	{
		return this.set(new Tuple2<String, String>(account.getAddress(), new String(transaction.getSignature())), transaction.getSignature());
	}
	
	public void remove(Account account, Transaction transaction)
	{
		this.delete(new Tuple2<String, String>(account.getAddress(), new String(transaction.getSignature())));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<byte[]> get(String address, int limit)
	{
		List<byte[]> signTransactions = new ArrayList<byte[]>();
		
		try
		{
			Map<Tuple2<String, String>, byte[]> accountTransactions = ((BTreeMap) this.map).subMap(
					Fun.t2(address, null),
					Fun.t2(address, Fun.HI()));
			
			//GET ITERATOR
			Iterator<byte[]> iterator = accountTransactions.values().iterator();
			
			//RETURN {LIMIT} TRANSACTIONS
			int counter = 0;
			while(iterator.hasNext() && ((counter < limit) || limit == -1))
			{
				signTransactions.add(iterator.next());
				counter++;
			}
		}
		catch(Exception e)
		{
			//ERROR
			e.printStackTrace();
		}
		
		return signTransactions;
	}
}

