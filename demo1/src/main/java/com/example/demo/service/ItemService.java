package com.example.demo.service;

import com.example.demo.dto.request.ItemBuy;
import com.example.demo.dto.request.ItemSave;
import com.example.demo.dto.response.ItemResponse;
import com.example.demo.entity.Item;
import com.example.demo.entity.Member;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    /*
     * 아이템 등록
     */
    public boolean itemSave(Authentication authentication, ItemSave itemSave){
        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 이메일입니다."));

        Item item = new Item(itemSave.name(), itemSave.price(), itemSave.itemState(), member);

        return item.getName().equals(itemRepository.save(item).getName());
    }

    /*
     * 아이템 구매 및 예약시 상태값 수정
     */
    public boolean itemBuy(ItemBuy itemBuy) {
        Item item = itemRepository.findById(itemBuy.id())
                .orElseThrow();

        item.changeItemState(itemBuy.itemState());

        return item.getItemState().equals(itemBuy.itemState());
    }

    @Transactional(readOnly = true)
    public ItemResponse findOne(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow();

        ItemResponse itemResponse = new ItemResponse(item);

        return itemResponse;
    }

    @Transactional(readOnly = true)
    public Stream<ItemResponse> findAll(Pageable pageable) {
        Page<Item> item = itemRepository.findAll(pageable);

        return item.stream().map((s) -> new ItemResponse(s));
    }
}
