package com.cmrs.service.impl;

import com.cmrs.dto.CategoryDTO;
import com.cmrs.entity.Category;
import com.cmrs.entity.FAQ;
import com.cmrs.mapper.CategoryMapper;
import com.cmrs.mapper.FAQMapper;
import com.cmrs.service.CategoryService;
import com.cmrs.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private FAQMapper faqMapper;

    /**
     * 新增报修分类
     * @param categoryDTO
     * @return
     */
    public void addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        int total = categoryMapper.getTotalOfLevel(categoryDTO.getParentId()) + 1;
        category.setSortOrder(total);

        categoryMapper.insert(category);
        
        // 随后进行常见问题添加
        if(categoryDTO.getParentId() == 0){
            // 如果添加的是根分类
            FAQ faq = new FAQ();
            faq.setTitle(categoryDTO.getName() );   //设置常见问题的标题
            faqMapper.insert(faq);
        }else {
            log.info(categoryDTO.getName());
            log.info("{}",categoryDTO.getParentId());
            // 如果不是根分类，那就先寻找对应的根分类
            category = categoryMapper.getById(categoryDTO.getParentId());
            // 通过根分类的名称在常见问题中查找对应的标题，然后将对应的常见问题提取出来
            FAQ faq = new FAQ();
            faq = faqMapper.getByTitle(category.getName());
            // 将现在的原因与新添加的二级分类进行拼接
            String reason = faq.getReason();
            if(reason == null){
                reason = categoryDTO.getName();
            }else {
                reason = reason + "；" + categoryDTO.getName();
            }
            faq.setReason(reason);
            // 最后进行更新操作
            faqMapper.update(faq);
        }
    }

    /**
     * 编辑报修分类
     * @param categoryDTO
     * @return
     */
    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        // 先检查传过来的报修分类的父级ID有没有变化
        int parentId =  categoryMapper.getParentIdById(categoryDTO.getId());
        if(parentId != categoryDTO.getParentId()) {
            // 如果发生变化，则看它是变为了一级分类还是某一二级分类(0或1、2、3、4)
            if(categoryDTO.getParentId() == 0) {
                int total = categoryMapper.getTotalOfLevel(categoryDTO.getParentId()) + 1;
                category.setSortOrder(total);
            }else {
                int total = categoryMapper.getTotalOfLevel(categoryDTO.getParentId()) + 1;
                category.setSortOrder(total);
            }
            categoryMapper.update(category);
        }
        categoryMapper.update(category);
    }


    /**
     * 删除报修分类
     * @param id
     * @return
     */
    public void deleteCategory(int id) {
        Category category = categoryMapper.getById(id);

        if(category.getParentId() == 0) {
            // 如果该分类为根分类,就要查询它的子分类，然后逐个删除
            List<Category> list = categoryMapper.getByParentId(id);
            for(Category c1 : list) {
                categoryMapper.deleteById(c1.getId());
            }
            // 最后再删除根分类
            categoryMapper.deleteById(id);

            // 与之对应的常见问题那边也要进行删除操作
            FAQ faq = faqMapper.getByTitle(category.getName());
            faqMapper.deleteByID(faq.getId());
        }else {
            // 删除前先删除常见问题那一边
            Category parentC = categoryMapper.getById(category.getParentId());
            FAQ faq = faqMapper.getByTitle(parentC.getName());
            String reason = faq.getReason();
            String toRemove = category.getName();

            // 使用正则匹配三种情况
            String regex = "(；" + Pattern.quote(toRemove) + "|" + Pattern.quote(toRemove) + "；|" + Pattern.quote(toRemove) + ")";
            String result = reason.replaceAll(regex, "");

            // 清理多余分号
            result = result.replaceAll("；；+", "；");
            result = result.replaceAll("^；|；$", "");

            // 最后执行更新操作
            faq.setReason(result);
            faqMapper.update(faq);

            // 如果不是根分类就直接删除
            categoryMapper.deleteById(id);
        }
    }


    /**
     * 获取报修分类以展示给用户
     * @return
     */
    public List<CategoryVO> list() {
        return categoryMapper.list();
    }

}
