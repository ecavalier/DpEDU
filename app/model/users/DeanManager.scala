package model.users

import com.mongodb.casbah.commons.TypeImports.ObjectId


case class DeanManager(id: ObjectId = new ObjectId,
                       email: String,
                       var password: String ="",
                       var theme: String = "bootstrap.min.css",
                       var avatar: String = "@routes.Assets.at(\"images/anonymous.jpg\")") extends User
