package com.noqapp.view.form;

import com.noqapp.common.utils.ScrubbedInput;

import java.io.Serializable;

/**
 * hitender
 * 2018-12-30 00:40
 */
public class PublishArticleForm extends FileUploadForm implements Serializable {

    private ScrubbedInput articleTitle;
    private String article;

    private PublishArticleForm() {
    }

    public static PublishArticleForm newInstance() {
        return new PublishArticleForm();
    }

    public ScrubbedInput getArticleTitle() {
        return articleTitle;
    }

    public PublishArticleForm setArticleTitle(ScrubbedInput articleTitle) {
        this.articleTitle = articleTitle;
        return this;
    }

    public String getArticle() {
        return article;
    }

    public PublishArticleForm setArticle(String article) {
        this.article = article;
        return this;
    }
}
