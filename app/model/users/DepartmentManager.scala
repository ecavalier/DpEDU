package model.users

import com.mongodb.casbah.commons.TypeImports.ObjectId


case class DepartmentManager(id: ObjectId = new ObjectId,
                             email: String,
                             var password: String = "",
                             var theme: String = "bootstrap.min.css",
                             departmentId: ObjectId,
                             var avatar: String = "@routes.Assets.at(\"images/anonymous.jpg\")") extends User
