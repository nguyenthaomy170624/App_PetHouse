import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class OrderDetails(): Serializable {
    var userUid: String? = null
    var userName: String? = null
    var prodNames: MutableList<String>? = null
    var prodPrices: MutableList<String>? = null
    var prodImages: MutableList<String>? = null
    var prodQuantities: MutableList<Int>? = null
    var address: String? = null
    var totalPrice: String? = null
    var phoneNumber: String? = null
    var orderAccepted: Boolean = false
    var paymentReceived: Boolean = false
    var itemPushKey: String? = null
    var currentTime: Long = 0

    constructor(parcel: Parcel) : this() {
        userUid = parcel.readString()
        userName = parcel.readString()
        address = parcel.readString()
        totalPrice = parcel.readString()
        phoneNumber = parcel.readString()
        currentTime = parcel.readLong()
        itemPushKey = parcel.readString()
        orderAccepted = parcel.readByte() != 0.toByte()
        paymentReceived = parcel.readByte() != 0.toByte()
    }

    constructor(
        userId: String,
        name: String,
        prodName: ArrayList<String>,
        prodPrice: ArrayList<String>,
        prodImage: ArrayList<String>,
        prodQuantities: ArrayList<Int>,
        address: String,
        totalAmount: String,
        phone: String,
        time: Long,
        itemPushKey: String?,
        orderAccepted: Boolean,
        paymentReceived: Boolean,
    ) : this() {
        this.userUid = userId
        this.userName = name
        this.prodNames = prodName
        this.prodPrices = prodPrice
        this.prodImages = prodImage
        this.prodQuantities = prodQuantities
        this.address = address
        this.totalPrice = totalAmount
        this.phoneNumber = phone
        this.currentTime = time
        this.itemPushKey = itemPushKey
        this.orderAccepted = orderAccepted
        this.paymentReceived = paymentReceived
    }

    fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userUid)
        parcel.writeString(userName)
        parcel.writeString(address)
        parcel.writeString(totalPrice)
        parcel.writeString(phoneNumber)
        parcel.writeLong(currentTime)
        parcel.writeString(itemPushKey)
        parcel.writeByte(if (orderAccepted) 1 else 0)
        parcel.writeByte(if (paymentReceived) 1 else 0)
    }

    fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails {
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetails?> {
            return arrayOfNulls(size)
        }
    }
}
