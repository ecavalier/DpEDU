package model.users

import org.bson.types.ObjectId

case class DeanManager (id: ObjectId = new ObjectId,
                            email: String,
                            password: String,
                            var theme: String =  "bootstrap.min.css") extends User
