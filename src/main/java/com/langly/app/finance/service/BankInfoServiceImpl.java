package com.langly.app.finance.service;

import com.langly.app.finance.entity.BankInfo;
import com.langly.app.finance.repository.BankInfoRepository;
import com.langly.app.finance.web.dto.BankInfoResponse;
import com.langly.app.finance.web.dto.BankInfoUpdateRequest;
import com.langly.app.finance.web.mapper.BankInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankInfoServiceImpl implements BankInfoService {

    private final BankInfoRepository bankInfoRepository;
    private final BankInfoMapper bankInfoMapper;

    private BankInfo getOrCreate() {
        return bankInfoRepository.findAll().stream().findFirst().orElseGet(() -> {
            BankInfo created = new BankInfo();
            created.setBankName("");
            created.setAccountHolder("");
            created.setIban("");
            created.setMotive("");
            return bankInfoRepository.save(created);
        });
    }

    @Override
    public BankInfoResponse get() {
        return bankInfoMapper.toResponse(getOrCreate());
    }

    @Override
    @Transactional
    public BankInfoResponse update(BankInfoUpdateRequest request) {
        BankInfo info = getOrCreate();
        bankInfoMapper.updateEntity(info, request);
        BankInfo saved = bankInfoRepository.save(info);
        return bankInfoMapper.toResponse(saved);
    }
}
