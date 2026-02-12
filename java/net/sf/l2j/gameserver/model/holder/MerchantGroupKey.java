package net.sf.l2j.gameserver.model.holder;

import java.util.Objects;

public class MerchantGroupKey
{
	private final String _category;
	private final String _grade;
	
	public MerchantGroupKey(String category, String grade)
	{
		_category = category == null ? "" : category.toLowerCase();
		_grade = grade == null ? "" : grade.toUpperCase();
	}
	
	public String getCategory()
	{
		return _category;
	}
	
	public String getGrade()
	{
		return _grade;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(_category, _grade);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		
		if (!(o instanceof MerchantGroupKey))
			return false;
		
		MerchantGroupKey other = (MerchantGroupKey) o;
		
		return _category.equals(other._category) && _grade.equals(other._grade);
	}
	
	@Override
	public String toString()
	{
		return _category + ":" + _grade;
	}
}