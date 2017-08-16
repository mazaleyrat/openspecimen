package com.krishagni.catissueplus.core.common.errors;

public enum PrintRuleConfigErrorCode implements ErrorCode{
	NOT_FOUND,

	ID_REQ,

	OBJECT_TYPE_REQ,

	INVALID_OBJECT_TYPE,

	RULES_REQ,

	CMD_FILES_DIR_REQ;

	@Override
	public String code() {
		return "PRINT_RULE_CFG_" + this.name();
	}
}
