package com.planet_ink.coffee_mud.Items.MiscMagic;

import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.Basic.GenItem;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

/*
   Copyright 2003-2020 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
public class GenMagicInstrument extends GenItem implements MusicalInstrument, MiscMagic, Wand
{
	@Override
	public String ID()
	{
		return "GenMagicInstrument";
	}

	private InstrumentType	type		= InstrumentType.OTHER_INSTRUMENT_TYPE;
	protected String		spellList	= "";
	protected int			maxUses		= Integer.MAX_VALUE;
	protected int			enchType	= -1;

	public GenMagicInstrument()
	{
		super();
		setName("a magical musical instrument");
		basePhyStats.setWeight(12);
		setDisplayText("a magical musical instrument sits here.");
		setDescription("");
		baseGoldValue = 15;
		basePhyStats().setLevel(1);
		recoverPhyStats();
		setMaterial(RawMaterial.RESOURCE_OAK);
	}

	@Override
	public void recoverPhyStats()
	{
		CMLib.flags().setReadable(this, false);
		super.recoverPhyStats();
	}

	@Override
	public InstrumentType getInstrumentType()
	{
		return type;
	}

	@Override
	public String getInstrumentTypeName()
	{
		return type.name();
	}

	@Override
	public void setSpell(final Ability theSpell)
	{
		spellList="";
		if(theSpell!=null)
			spellList=theSpell.ID();
	}

	@Override
	public Ability getSpell()
	{
		if((spellList==null)||(spellList.length()==0))
			return null;
		return CMClass.getAbility(spellList);
	}

	@Override
	public int maxUses()
	{
		return maxUses;
	}

	@Override
	public void setMaxUses(final int newMaxUses)
	{
		maxUses = newMaxUses;
	}

	@Override
	public int getEnchantType()
	{
		return enchType;
	}

	@Override
	public void setEnchantType(final int enchType)
	{
		this.enchType = enchType;
	}

	@Override
	public boolean checkWave(final MOB mob, final String message)
	{
		return StdWand.checkWave(mob, message, this);
	}

	@Override
	public void waveIfAble(final MOB mob, final Physical afftarget, final String message)
	{
		StdWand.waveIfAble(mob, afftarget, message, this);
	}


	@Override
	public String magicWord()
	{
		return "";
	}

	@Override
	public String text()
	{
		return CMLib.coffeeMaker().getPropertiesStr(this,false);
	}

	@Override
	public void setReadableText(final String text)
	{
		super.setReadableText(text);
		if(text.length()>0)
		{
			if(CMath.isInteger(text))
			{
				setInstrumentType(CMath.s_int(text));
				super.setReadableText("");
			}
			else
			{
				final Ability A = CMClass.getAbility(text);
				if(A != null)
				{
					setSpell(A);
					super.setReadableText("");
				}
			}
		}
	}

	@Override
	public String readableText()
	{
		return super.readableText();
	}

	@Override
	public void setInstrumentType(final int typeOrdinal)
	{
		if(typeOrdinal < InstrumentType.values().length)
			type = InstrumentType.values()[typeOrdinal];
	}

	@Override
	public void setInstrumentType(final InstrumentType newType)
	{
		if(newType != null)
			type = newType;
	}

	@Override
	public void setInstrumentType(final String newType)
	{
		if(newType != null)
		{
			final InstrumentType typeEnum = (InstrumentType)CMath.s_valueOf(InstrumentType.class, newType.toUpperCase().trim());
			if(typeEnum != null)
				type = typeEnum;
		}
	}

	@Override
	public boolean okMessage(final Environmental host, final CMMsg msg)
	{
		if (!super.okMessage(host, msg))
			return false;
		if(amWearingAt(Wearable.WORN_WIELD)
		&&(msg.source()==owner())
		&&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
		&&(msg.source().location()!=null)
		&&((msg.tool()==null)
			||(msg.tool()==this)
			||(!(msg.tool() instanceof Weapon))
			||(((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_NATURAL)))
		{
			msg.source().location().show(msg.source(), null, this, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> play(s) <O-NAME>."));
			return false;
		}
		return true;
	}

	private final static String[] MYCODES={"ENCHTYPE", "SPELL"};

	@Override
	public String getStat(final String code)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			return CMLib.coffeeMaker().getGenItemStat(this,code);
		switch(getCodeNum(code))
		{
		case 0:
			if((getEnchantType()<0)||(getEnchantType()>=Ability.ACODE_DESCS_.length))
				return "ANY";
			return Ability.ACODE_DESCS_[getEnchantType()];
		case 1:
		{
			final Ability A = getSpell();
			return (A!=null) ? A.ID() : "";
		}
		default:
			return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
		}
	}

	@Override
	public void setStat(final String code, final String val)
	{
		if(CMLib.coffeeMaker().getGenItemCodeNum(code)>=0)
			CMLib.coffeeMaker().setGenItemStat(this,code,val);
		else
		switch(getCodeNum(code))
		{
		case 0:
			setEnchantType(CMParms.indexOf(Ability.ACODE_DESCS_, val.toUpperCase().trim()));
			break;
		case 1:
		{
			final Ability A=CMClass.getAbility(val);
			if(A!=null)
				setSpell(A);
			break;
		}
		default:
			CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
			break;
		}
	}

	@Override
	protected int getCodeNum(final String code)
	{
		for(int i=0;i<MYCODES.length;i++)
		{
			if(code.equalsIgnoreCase(MYCODES[i]))
				return i;
		}
		return -1;
	}

	private static String[]	codes	= null;

	@Override
	public String[] getStatCodes()
	{
		if(codes!=null)
			return codes;
		final String[] MYCODES=CMProps.getStatCodesList(GenMagicInstrument.MYCODES,this);
		final String[] superCodes=CMParms.toStringArray(GenericBuilder.GenItemCode.values());
		codes=new String[superCodes.length+MYCODES.length];
		int i=0;
		for(;i<superCodes.length;i++)
			codes[i]=superCodes[i];
		for(int x=0;x<MYCODES.length;i++,x++)
			codes[i]=MYCODES[x];
		return codes;
	}

	@Override
	public boolean sameAs(final Environmental E)
	{
		if(!(E instanceof GenMagicInstrument))
			return false;
		final String[] codes=getStatCodes();
		for(int i=0;i<codes.length;i++)
		{
			if(!E.getStat(codes[i]).equals(getStat(codes[i])))
				return false;
		}
		return true;
	}
}
