package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.parsing.io.SourcePrinter;

public class FunctionElement extends PropertyElement
{
	private List<ParameterElement> _parameters;
	private List<String> _references;
	private List<ExceptionElement> _exceptions;
	private List<ReturnTypeElement> _returnTypes;

	private boolean _isConstructor;
	private boolean _isMethod;

	/**
	 * FunctionElement
	 */
	public FunctionElement()
	{
	}

	/**
	 * addException
	 * 
	 * @param currentException
	 */
	public void addException(ExceptionElement exception)
	{
		if (exception != null)
		{
			if (this._exceptions == null)
			{
				this._exceptions = new ArrayList<ExceptionElement>();
			}

			this._exceptions.add(exception);
		}
	}

	/**
	 * addParameter
	 * 
	 * @param parameter
	 */
	public void addParameter(ParameterElement parameter)
	{
		if (parameter != null)
		{
			if (this._parameters == null)
			{
				this._parameters = new ArrayList<ParameterElement>();
			}

			this._parameters.add(parameter);
		}
	}

	/**
	 * addReferences
	 * 
	 * @param reference
	 */
	public void addReference(String reference)
	{
		if (reference != null && reference.length() > 0)
		{
			if (this._references == null)
			{
				this._references = new ArrayList<String>();
			}

			this._references.add(reference);
		}
	}

	/**
	 * addReturnType
	 * 
	 * @param returnType
	 */
	public void addReturnType(ReturnTypeElement returnType)
	{
		if (returnType != null)
		{
			if (this._returnTypes == null)
			{
				this._returnTypes = new ArrayList<ReturnTypeElement>();
			}
			
			if (this._returnTypes.contains(returnType) == false)
			{
				this._returnTypes.add(returnType);
			}
		}
	}

	/**
	 * addReturnType
	 * 
	 * @param type
	 */
	public void addReturnType(String type)
	{
		if (type != null && type.length() > 0)
		{
			ReturnTypeElement returnType = new ReturnTypeElement();

			returnType.setType(type);

			this.addReturnType(returnType);
		}
	}
	
	/**
	 * getExceptions
	 * 
	 * @return
	 */
	public List<ExceptionElement> getExceptions()
	{
		List<ExceptionElement> result = this._exceptions;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getExceptionTypes
	 * 
	 * @return
	 */
	public List<String> getExceptionTypes()
	{
		List<String> result = new ArrayList<String>();

		for (ExceptionElement exception : this.getExceptions())
		{
			result.add(exception.getType());
		}

		return result;
	}

	/**
	 * getParameters
	 * 
	 * @return
	 */
	public List<ParameterElement> getParameters()
	{
		List<ParameterElement> result = this._parameters;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getParameterTypes
	 * 
	 * @return
	 */
	public List<String> getParameterTypes()
	{
		List<String> result = new ArrayList<String>();

		for (ParameterElement parameter : this.getParameters())
		{
			result.add(StringUtil.join(JSTypeConstants.PARAMETER_TYPE_DELIMITER, parameter.getTypes()));
		}

		return result;
	}

	/**
	 * getReferences
	 * 
	 * @return
	 */
	public List<String> getReferences()
	{
		List<String> result = this._references;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getReturnTypes
	 * 
	 * @return
	 */
	public List<ReturnTypeElement> getReturnTypes()
	{
		List<ReturnTypeElement> result = this._returnTypes;
		
		if (result == null)
		{
			result = Collections.emptyList();
		}
		
		return result;
	}
	
	/**
	 * getReturnTypeNames
	 * 
	 * @return
	 */
	public List<String> getReturnTypeNames()
	{
		List<String> result;

		if (this._returnTypes != null)
		{
			result = new ArrayList<String>(this._returnTypes.size());

			for (ReturnTypeElement type : this._returnTypes)
			{
				result.add(type.getType());
			}
		}
		else
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getSignature
	 * 
	 * @return
	 */
	public String getSignature()
	{
		StringBuilder buffer = new StringBuilder();
		boolean first = true;

		// include actual type in custom notation, if not a function
		List<String> types = this.getTypeNames();
		
		if (types != null && types.size() > 0)
		{
			buffer.append(StringUtil.join(JSTypeConstants.RETURN_TYPE_DELIMITER, types));
		}
		else
		{
			buffer.append(JSTypeConstants.FUNCTION_TYPE);
		}

		// include return types
		for (ReturnTypeElement returnType : this.getReturnTypes())
		{
			buffer.append(first ? JSTypeConstants.FUNCTION_SIGNATURE_DELIMITER : JSTypeConstants.RETURN_TYPE_DELIMITER); //$NON-NLS-1$ //$NON-NLS-2$
			buffer.append(returnType.getType());
			first = false;
		}

		return buffer.toString();
	}

	/**
	 * hasExceptions
	 * 
	 * @return
	 */
	public boolean hasExceptions()
	{
		return this._exceptions != null && this._exceptions.isEmpty() == false;
	}

	/**
	 * hasParameters
	 * 
	 * @return
	 */
	public boolean hasParameters()
	{
		return this._parameters != null && this._parameters.isEmpty() == false;
	}

	/**
	 * isConstructor
	 * 
	 * @return
	 */
	public boolean isConstructor()
	{
		return this._isConstructor;
	}

	/**
	 * isMethod
	 * 
	 * @return
	 */
	public boolean isMethod()
	{
		return this._isMethod;
	}

	/**
	 * setIsConstructor
	 * 
	 * @param value
	 */
	public void setIsConstructor(boolean value)
	{
		this._isConstructor = value;
	}

	/**
	 * setIsMethod
	 * 
	 * @param value
	 */
	public void setIsMethod(boolean value)
	{
		this._isMethod = value;
	}

	/**
	 * toSource
	 * 
	 * @param printer
	 */
	public void toSource(SourcePrinter printer)
	{
		printer.printIndent();

		// print any annotations
		if (this.isInstanceProperty())
		{
			printer.print("static "); //$NON-NLS-1$
		}
		if (this.isInternal())
		{
			printer.print("internal "); //$NON-NLS-1$
		}
		if (this.isConstructor())
		{
			printer.print("constructor "); //$NON-NLS-1$
		}
		if (this.isMethod())
		{
			printer.print("method "); //$NON-NLS-1$
		}

		// print name
		printer.print(this.getName());
		
		// print parameter types
		printer.print("(").print(StringUtil.join(JSTypeConstants.PARAMETER_TYPE_DELIMITER, this.getParameterTypes())).print(")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// print return types
		List<String> returnTypes = this.getReturnTypeNames();
		
		printer.print(JSTypeConstants.FUNCTION_SIGNATURE_DELIMITER); //$NON-NLS-1$
		
		if (returnTypes != null && returnTypes.isEmpty() == false)
		{
			printer.print(StringUtil.join(JSTypeConstants.RETURN_TYPE_DELIMITER, returnTypes)); //$NON-NLS-1$
		}
		else
		{
			printer.print(JSTypeConstants.UNDEFINED_TYPE);
		}

		// print exceptions
		if (this.hasExceptions())
		{
			printer.print(" throws ").print(StringUtil.join(", ", this.getExceptionTypes())); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
