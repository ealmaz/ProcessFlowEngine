package kg.devcats.processflowengine.offline

import java.util.Date

object ProcessMocker {

    val mock = mutableMapOf<String, String>().apply {

        put(
            "start", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"IN_PROCESS",
          "app_bar_text":"Идентификация",
          "title":"Проверяем ваши \nдокументы",
          "description":""
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"ANY_BUTTON_ID",
                "text":"К идентификации",
                "style":"ACCENT",
                "disabled":true,
                "properties":{
                   
                }
             }
          },
          {
             "responseType":"RETRY",
             "responseItem":{
                "id":"RETRY_1",
                "properties": {
                    "showLoader":false,
                    "enableAt":${Date().time + 2000}
                }
             }
          }    
       ]
    }
""".trimIndent()
        )


        put(
            "retry1", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"COMPLETE",
          "app_bar_text":"Идентификация",
          "title":"Документы успешно \nпроверены",
          "description":"Перейдите к идендификации "
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"start_ident",
                "text":"К идентификации",
                "style":"ACCENT",
                "properties":{
                   "isEnabled":true
                }
             }
          }   
       ]
    }
""".trimIndent()
        )

        put(
            "start_ident", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"PASSPORT_FRONT_PHOTO",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"PASSPORT_FRONT_PHOTO2",
                "text":"далее",
                "style":"ACCENT"
             }
          }   
       ]
    }
""".trimIndent()
        )

        put(
            "start_lottie_url", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"IN_PROCESS",
          "app_bar_text":"Идентификация",
          "title":"Проверяем ваши \nдокументы",
          "description":"",
          "animationUrl":"https://minio.o.kg/media-service/light/relogin_ux_ui.json"
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"start_ident_foreign",
                "text":"Начать идентификацию",
                "style":"ACCENT",
                "disabled":false,
                "properties":{
                   
                }
             }
          }    
       ]
    }
""".trimIndent()
        )


        put(
            "start_ident_foreign", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"FOREIGN_PASSPORT_PHOTO",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"SELFIE_PHOTO",
                "text":"далее",
                "style":"ACCENT"
             }
          }   
       ]
    }
""".trimIndent()
        )



        put(
            "PASSPORT_FRONT_PHOTO2", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"PASSPORT_BACK_PHOTO",
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"PASSPORT_BACK_PHOTO",
                "text":"Далее",
                "style":"ACCENT"
             }
          } 
       ]
    }
""".trimIndent()
        )

        put(
            "PASSPORT_BACK_PHOTO", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"SELFIE_PHOTO",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"SELFIE_PHOTO",
                "text":"Далее",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )


        put(
            "SELFIE_PHOTO", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"SIMPLE_SELFIE_PHOTO",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"SIMPLE_SELFIE_PHOTO",
                "text":"Далее",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )


        put(
            "SIMPLE_SELFIE_PHOTO", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"COMPLETE",
          "app_bar_text":"Дополнительные сведения",
          "description":"Откуда у вас, простого смертного,\nстолько денег? Пожалуйста, \nпредоставьте документ о доходах."
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"make_simple_photo",
                "text":"Приложить фото",
                "style":"ACCENT",
                "properties":{
                   "isEnabled":true
                }
             }
          }   
       ]
    }
""".trimIndent()
        )


        put(
            "make_simple_photo", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"SIMPLE_CAMERA",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"SIMPLE_CAMERA",
                "text":"Далее",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )

        put(
            "SIMPLE_CAMERA", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"VIDEO_CALL_PROMO",
       "screen_state":{
          "status":null,
          "app_bar_text":"Идентификация",
          "title":"Осталось пройти \nвидеоидентификацию",
          "description":"Не забудьте приготовить паспорт."
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"RETURN_TO_QUEUE_LATER",
                "text":"Вернуться позже",
                "style":"SECONDARY"
             }
          },
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"VIDEO_IDENT_BUTTON",
                "text":"Пройти видеоидентификацию ",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )



        put(
            "VIDEO_IDENT_BUTTON", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"VIDEO_CALL",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"WEB_VIEW",
             "responseItem":{
                "id":"VIDEO_IDENT",
                "url":"https://mic-test.com"
             }
          }
       ]
    }
""".trimIndent()
        )

        put(
            "VIDEO_IDENT", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"IN_PROCESS",
          "app_bar_text":"Идентификация",
          "title":"Проверяем ваши \nдокументы",
          "description":""
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"WEB_VIEW_OFERTA",
                "text":"Далее ",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )


        put(
            "WEB_VIEW_OFERTA", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"WEB_VIEW",
       "screen_state":{
            "app_bar_text":"OFERTA"
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"WEB_VIEW",
             "responseItem":{
                "id":"ANY_WEB_VIEW_2",
                "url":"https://lk.o.kg/uploads/dogovor_ru.pdf",
                "properties":{
                    "fileType":"PDF",
                    "isShareEnabled":true
                }
             }
          },
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"ANY_WEB_VIEW_2",
                "text":"Далее ",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )

        put(
            "ANY_WEB_VIEW_2", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"IN_PROCESS",
          "app_bar_text":"Идентификация",
          "title":"Проверяем ваши \nдокументы",
          "description":""
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"OTP",
                "text":"Далее ",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )



        put(
            "OTP", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"INPUT_OTP",
       "screen_state":{
          "status":null,
          "app_bar_text":"Идентификация",
          "title":"",
          "description":"Подтвердите заявку.\n SMS с кодом отправлено на номер \n+996 700 000 999"
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"INPUT_FIELD",
             "responseItem":{
                "fieldId":"OTP_INPUT",
                "inputType":"NUMBER",
                "validations":[
                    {
                       "type":"REQUIRED",
                       "value":"true"
                    }
                ],
                "enableActionAfterMills":10000,
                "additionalActionResolutionCode":"RESEND_OTP",
                "isOtpView":"true",
                "errorMessage":"Неверный код"
             }
          }
       ]
    }
""".trimIndent()
        )


        put(
            "RESEND_OTP", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"INPUT_OTP",
       "screen_state":{
          "status":null,
          "app_bar_text":"Идентификация",
          "title":"",
          "description":"Подтвердите заявку.\n SMS с кодом отправлено на номер \n+996 700 000 999"
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"INPUT_FIELD",
             "responseItem":{
                "fieldId":"OTP_INPUT",
                "numberOfLines":6,
                "placeholder":"Добавьте комментарий"
             }
          }
       ]
    }
""".trimIndent()
        )



        put(
            "OTP_INPUT", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"INPUT_FORM",
       "screen_state":{
            "app_bar_text":"Passport data"
       },
       "messages":[],
       "allowed_answers":[
          {
            "responseType":"BUTTON",
            "responseItem":{
                "disabled":true,
                "style":"SECONDARY",
                "properties":null,
                "buttonId":"start_ident",
                "text":"Не продолжать идентификацию"
            }
          },
          {
            "responseType":"BUTTON",
            "responseItem":{
                "disabled":false,
                "style":"SECONDARY",
                "properties":null,
                "buttonId":"start_ident",
                "text":"Продолжить идентификацию"
            }
          },
          {
            "responseType":"INPUT_FORM",
            "responseItem":{
                "formId":"passport_form",
                "formItems":[
                    {
                        "formItemType":"LABEL",
                        "formItem":{
                            "fieldId":"passport_data",
                            "label":"ФИО"
                        }
                    },
                    {
                        "formItemType":"INPUT_FIELD",
                        "formItem":{
                            "fieldId":"PHONE",
                            "label":"Phone",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "value":"996700000697",
                            "mask":"996 XXX XXX XXX",
                            "maskSymbols":[" "],
                            "inputType":"NUMBER"
                        }
                    },
                    {
                        "formItemType":"INPUT_FIELD",
                        "formItem":{
                            "fieldId":"NAME",
                            "label":"Имя",
                            "placeholder":"Имя",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "value":"Prefilled value"
                        }
                    },
                    {
                        "formItemType":"INPUT_FIELD",
                        "formItem":{
                            "fieldId":"SURNAME",
                            "label":"Фамилия",
                            "placeholder":"Фамилия",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "value":"Prefilled value"
                        }
                    },
                    {
                        "formItemType":"LABEL",
                        "formItem":{
                            "fieldId":"passport_data",
                            "label":"Данные паспорта"
                        }
                    },
                    {
                        "formItemType":"INPUT_FIELD",
                        "formItem":{
                            "fieldId":"INN",
                            "label":"Персональный номер, состоящий из 14 цифр",
                            "placeholder":"ИНН",
                            "inputType":"NUMBER",
                            "validations":[
                                {
                                    "type":"REGEX",
                                    "value":"^[0-9]*"
                                },
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "value":"123123123"
                        }
                    },
                    {
                        "formItemType":"INPUT_FIELD",
                        "formItem":{
                            "fieldId":"PASSPORT",
                            "label":"Номер паспорта",
                            "placeholder":"Номер паспорта",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "value":"ID1234567"
                        }
                    },
                    {
                        "formItemType":"DATE_PICKER_FORM_ITEM",
                        "formItem":{
                            "fieldId":"PASSPORT_date",
                            "label":"Дата"
                        }
                    },
                    {
                        "formItemType":"LABEL",
                        "formItem":{
                            "fieldId":"pair_text_fields",
                            "label":"Pair text fields"
                        }
                    },
                    {
                        "formItemType":"PAIR_FIELD",
                        "formItem":{
                            "fieldId":"PAIR_FIELD_1",
                            "startText":"Экспресс кредит",
                            "endText":"150 343,00 <u>c</u> ",
                            "isHtml":true
                        }
                    },
                    {
                        "formItemType":"PAIR_FIELD",
                        "formItem":{
                            "fieldId":"PAIR_FIELD_2",
                            "startText":"Visa Gold \n•••• 2345",
                            "endText":"150 343,00 <u>c</u> "
                        }
                    },
                    {
                        "formItemType":"PAIR_FIELD",
                        "formItem":{
                            "fieldId":"PAIR_FIELD_3",
                            "startText":"Депозит 343,00 <u>c</u> ",
                            "endText":"Депозит Депозит Депозит150 343,00 <u>c</u> ",
                            "isHtml":true
                        }
                    },
                    {
                        "formItemType":"PAIR_FIELD",
                        "formItem":{
                            "fieldId":"PAIR_FIELD_4",
                            "startText":"Депозит Депозит Депозит Депозит Депозит ",
                            "endText":"Депозит_Депозит_Депозит 150 343,00 <u>c</u> "
                        }
                    },
                     {
                        "formItemType":"GROUP_BUTTON_FORM_ITEM",
                        "formItem":{
                            "fieldId":"agreement",
                            "label":"",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "chooseType":"MULTIPLE",
                            "buttonType":"CHECK_BOX",
                            "options":[
                                {
                                    "id":"nda6",
                                    "isHtmlText":"true",
                                    "label":"<b><font color='#000000'>Список карт для переноса правильный</font></b> <br>Если у вас есть карты в Halyk Bank, уберите галочку. Оформим заявку на проверку и свяжемся c вами",
                                    "isSelected":"true"
                                }
                            ]
                        }
                    }
                ]
            }
          }
       ]
    }
""".trimIndent()
        )




        put(
            "passport_form", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"INPUT_FORM",
       "screen_state":{
            "app_bar_text":"Passport data"
       },
       "messages":[],
       "allowed_answers":[
          {
            "responseType":"INPUT_FORM",
            "responseItem":{
                "formId":"passport_form2",
                "formItems":[
                    {
                        "formItemType":"GROUP_BUTTON_FORM_ITEM",
                        "formItem":{
                            "fieldId":"agreement_4235",
                            "label":"",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "chooseType":"MULTIPLE",
                            "buttonType":"CHECK_BOX",
                            "options":[
                                {
                                    "id":"nda6",
                                    "isHtmlText":"true",
                                    "label":"вцувцута цу ацу ацу а цуа цуа цу а",
                                    "isSelected":"true"
                                }
                            ]
                        }
                    },
                     {
                        "formItemType":"GROUP_BUTTON_FORM_ITEM",
                        "formItem":{
                            "fieldId":"agreement",
                            "label":"",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "chooseType":"MULTIPLE",
                            "buttonType":"CHECK_BOX",
                            "options":[
                                {
                                    "id":"nda6",
                                    "isHtmlText":"false",
                                    "label":"By pressing Select number you confirm y consent: to terms and conditions of communication services",
                                    "isSelected":"true"
                                }
                            ]
                        }
                    },
                    {
                        "formItemType":"GROUP_BUTTON_FORM_ITEM",
                        "formItem":{
                            "fieldId":"agreement_4",
                            "label":"",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "chooseType":"MULTIPLE",
                            "buttonType":"CHECK_BOX",
                            "options":[
                                {
                                    "id":"nda632",
                                    "isHtmlText":"true",
                                    "label":"By pressing \"Select number\" you confirm your consent: <a href=\"https://lk.o.kg/uploads/dogovor_ru.pdf\"> to terms and conditions of communication services",
                                    "isSelected":"true"
                                }
                            ]
                        }
                    },
                    {
                        "formItemType":"GROUP_BUTTON_FORM_ITEM",
                        "formItem":{
                            "fieldId":"agreement_7",
                            "label":"",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "chooseType":"MULTIPLE",
                            "buttonType":"CHECK_BOX",
                            "options":[
                                {
                                    "id":"nda23216",
                                    "isHtmlText":"true",
                                    "label":"By pressing \"Select number\" you confirm your consent: <a href=\"https://lk.o.kg/uploads/dogovor_ru.pdf\"> to terms and conditions of communication services",
                                    "isSelected":"true"
                                }
                            ]
                        }
                    },
                    {
                        "formItemType":"GROUP_BUTTON_FORM_ITEM",
                        "formItem":{
                            "fieldId":"agreement_2",
                            "label":"",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "chooseType":"MULTIPLE",
                            "buttonType":"TOGGLE",
                            "options":[
                                {
                                    "id":"nda4",
                                    "isHtmlText":"true",
                                    "isSelected":"true",
                                    "label":"By pressing \"Select number\" you confirm your consent: <a href=\"https://dengi.kg/soglasie_pers_dannie/en/light/\"> to terms and conditions of communication services"
                                },
                                {
                                    "id":"nda3",
                                    "isSelected":"true",
                                    "label":"By pressing \"Select number\" you confirm your consent: <a href=\"https://dengi.kg/soglasie_pers_dannie/en/?theme=light\"> to terms and conditions of communication services",
                                    "isHtmlText":"true"
                                }
                            ]
                        }
                    },
                    {
                        "formItemType":"GROUP_BUTTON_FORM_ITEM",
                        "formItem":{
                            "fieldId":"agreement_3",
                            "label":"",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "chooseType":"SINGLE",
                            "buttonType":"RADIO_BUTTON",
                            "options":[
                                {
                                    "id":"nda1",
                                    "isHtmlText":"true",
                                    "label":"By pressing \"Select number\" you confirm your consent: <a href=\"https://dengi.kg/soglasie_pers_dannie/en/light/\"> to terms and conditions of communication services"
                                },
                                {
                                    "id":"nda2",
                                    "label":"By pressing \"Select number\" you confirm your consent: <a href=\"https://dengi.kg/soglasie_pers_dannie/en/light/\"> to terms and conditions of communication services",
                                    "isHtmlText":"true",
                                    "isSelected":"true"
                                }
                            ]
                        }
                    }
                ]
            }
          }
       ]
    }
""".trimIndent()
        )




        put(
            "passport_form2", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"INPUT_FORM",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
            "responseType":"INPUT_FORM",
            "responseItem":{
                "formId":"address",
                "formItems":[
                    {
                        "formItemType":"LABEL",
                        "formItem":{
                            "fieldId":"address_live",
                            "label":"Укажите место жительства"
                        }
                    },
                     {
                        "formItemType":"DROP_DOWN_FORM_ITEM",
                        "formItem":{
                            "fieldId":"CHOOSE_REGION_1",
                            "chooseType":"SINGLE",
                            "label":"Область",
                            "isNeedToFetchOptions":true,
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ]
                        }
                    },
                    {
                        "formItemType":"DROP_DOWN_FORM_ITEM",
                        "formItem":{
                            "fieldId":"CHOOSE_REGION",
                            "chooseType":"SINGLE",
                            "parentFieldId":"CHOOSE_REGION_1",
                            "label":"Регион",
                            "isNeedToFetchOptions":true,
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ]
                        }
                    },
                    {
                        "formItemType":"DROP_DOWN_FORM_ITEM",
                        "formItem":{
                            "fieldId":"CHOOSE_CITY",
                            "parentFieldId":"CHOOSE_REGION",
                            "chooseType":"SINGLE",
                            "label":"Город",
                            "isNeedToFetchOptions":true
                        }
                    }, 
                    {
                        "formItemType":"INPUT_FIELD",
                        "formItem":{
                            "fieldId":"street",
                            "placeholder":"Street",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "value":"Prefilled value"
                        }
                    },
                    {
                        "formItemType":"INPUT_FIELD",
                        "formItem":{
                            "fieldId":"house",
                            "placeholder":"Номер дома",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ],
                            "value":"123"
                        }
                    },
                    {
                        "formItemType":"INPUT_FIELD",
                        "formItem":{
                            "fieldId":"apparment_number",
                            "placeholder":"Номер квартиры",
                            "value":"123"
                        }
                    },
                    {
                        "formItemType":"GROUP_BUTTON_FORM_ITEM",
                        "formItem":{
                            "fieldId":"same_adress",
                            "label":"",
                            "chooseType":"MULTIPLE",
                            "buttonType":"CHECK_BOX",
                            "options":[
                                {
                                    "id":"nda612",
                                    "label":"Адрес места жительства совпадает с адресом прописки",
                                    "isSelected":"true"
                                }
                            ]
                        }
                    }
                ]
            }
          }
       ]
    }
""".trimIndent()
        )


        put(
            "address", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"COMPLETE",
          "app_bar_text":"Идентификация",
          "title":"Документы успешно \nсформированы",
          "description":""
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"CREATE_qweqwew_APPLICATION",
                "text":"Создать заявку",
                "style":"ACCENT",
                "properties":{
                    
                }
             }
          }
       ]
    }
""".trimIndent()
        )

        put(
            "CREATE_qweqwew_APPLICATION", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"WEB_VIEW",
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"WEB_VIEW",
             "responseItem":{
                "id":"ANY_WEB_VIEW_ID",
                "url":"https://google.com"
             }
          }
       ]
    }
""".trimIndent()
        )



        put(
            "ANY_WEB_VIEW_ID", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"REJECTED",
          "app_bar_text":"Timer",
          "title":"Личность \nне подтверждена",
          "description":"Подтвердите личность для безопасности кошелька и доступа ко всем услугам",
          "timer":"${Date().time + 30000}",
          "timerText":"Повторить через: "
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"TIMER_STATUS",
                "text":"Повторить",
                "style":"ACCENT",
                "properties":{
                   "enableAt":"${Date().time + 30000}"
                }
             }
          }   
       ]
    }
""".trimIndent()
        )



        put(
            "TIMER_STATUS", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"COMPLETE",
          "app_bar_text":"Идентификация",
          "title":"Документы успешно \nсформированы",
          "description":""
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"EXIT_NAVIGATE_TO_WALLET_MAIN",
                "text":"На главную",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )

    }

}