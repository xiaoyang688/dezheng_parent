package com.dezheng.dto;

public class AnswerDTO {

    /**
     * msg_body : {"text":{"content":"测试文本消息"}}
     * user_id : string
     */

    private MsgBodyBean msg_body;
    private String user_id;

    public MsgBodyBean getMsg_body() {
        return msg_body;
    }

    public void setMsg_body(MsgBodyBean msg_body) {
        this.msg_body = msg_body;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public static class MsgBodyBean {
        /**
         * text : {"content":"测试文本消息"}
         */

        private TextBean text;

        public TextBean getText() {
            return text;
        }

        public void setText(TextBean text) {
            this.text = text;
        }

        public static class TextBean {
            /**
             * content : 测试文本消息
             */

            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}
