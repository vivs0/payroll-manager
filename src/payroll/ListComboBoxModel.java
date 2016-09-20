package payroll;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/*
 * Class for storing the model for which all combo boxes in the program
 * will adhere to.
 */

public class ListComboBoxModel implements MutableComboBoxModel, ActionListener 
{
	protected ArrayList<String> names;
	protected Object item;
	protected ArrayList listeners;
	
	public ListComboBoxModel(ArrayList<String> list) 
	{
		this.listeners = new ArrayList<String>();
		this.names = list;
		if(list.size() > 0) 
		{
			item = list.get(0);
		}
	}
	
	public void setSelectedItem(Object item) 
	{
		this.item = item;
	}
	public Object getSelectedItem() 
	{
		return this.item;
	}

	public Object getElementAt(int index) 
	{
		return names.get(index);
	}
	public int getSize() 
	{
		return names.size();
	}

	public void addListDataListener(ListDataListener l) 
	{
		listeners.add(l);
	}
	public void removeListDataListener(ListDataListener l) 
	{
		this.listeners.remove(l);
	}
	
	public void actionPerformed(ActionEvent evt) 
	{
		if(evt.getActionCommand().equals("update")) 
		{
			this.fireUpdate();
		}
	}
//Updates contents of the combo box when entry is added or deleted.
	public void fireUpdate() 
	{
		ListDataEvent le = new ListDataEvent(this,
			ListDataEvent.CONTENTS_CHANGED,
			0,
			names.size());
		for(int i=0; i<listeners.size(); i++) 
		{
			ListDataListener l = (ListDataListener)listeners.get(i);
			l.contentsChanged(le);
		}
	}

	@Override
	public void addElement(Object o)
	{
		names.add((String) o);
		fireUpdate();
	}

	@Override
	public void insertElementAt(Object o, int arg1)
	{
		names.add(arg1, (String) o);
		fireUpdate();
	}

	@Override
	public void removeElement(Object arg0)
	{
		
	}

	@Override
	public void removeElementAt(int arg0)
	{
		names.remove(arg0);
		fireUpdate();
	}
}
