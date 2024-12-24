package pl.rotkom.friday.thirdparty.etherscan.api.service;

import java.util.List;

import pl.rotkom.friday.thirdparty.common.rq.RequestResult;
import pl.rotkom.friday.thirdparty.etherscan.api.dto.AccountTxDTO;

public interface IEtherscanApi {

    RequestResult<List<AccountTxDTO>> getAccountTransactions(String address);
}
