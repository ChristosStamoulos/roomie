package com.example.roomie.frontend.Adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomie.R
import com.example.roomie.backend.domain.Room
import com.example.roomie.backend.utils.Pair

class RoomsAdapter (private val roomList: ArrayList<Pair<Room, ArrayList<ByteArray>>>, private val listener: onRoomClickListener) : RecyclerView.Adapter<RoomsAdapter.RoomViewHolder>() {
    interface onRoomClickListener {
        fun onRoomClick(room: Room, view: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rooms_homescreen, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomList[position]
        holder.bindRoom(room, listener)
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var roomName: TextView
        var roomImage: ImageView? = null
        var roomArea: TextView
        var roomStars: TextView

        init {
            roomName = itemView.findViewById(R.id.name)
            roomArea = itemView.findViewById(R.id.roomArea)
            roomStars = itemView.findViewById(R.id.roomStars)
        }

        fun bindRoom(room: Pair<Room, ArrayList<ByteArray>>, listener: onRoomClickListener) {
            roomName = itemView.findViewById(R.id.name)
            roomImage = itemView.findViewById(R.id.roomImage)
            roomName.text = room.key.name
            roomArea.text = room.key.area
            roomStars.text = room.key.rating.toString()
            var bitmap: Bitmap? = BitmapFactory.decodeByteArray(room.value!![0], 0, room.value!![0].size)
            roomImage!!.setImageBitmap(bitmap)
            /* Set the image based on the resource identifier
            if (resId != 0) {
                roomImage!!.setImageResource(resId)
            }*/

            itemView.setOnClickListener {
                // Create the click animation
                it.animate()
                        .scaleX(0.9f) // Scale down the X axis to 90%
                        .scaleY(0.9f) // Scale down the Y axis to 90%
                        .setDuration(100) // Duration of the scale down animation
                        .withEndAction {
                            // Scale back to original size
                            it.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(100)
                                    .withEndAction {
                                        listener.onRoomClick(room.key, it)
                                    }
                                    .start()
                        }
                        .start()
            }
        }
    }
}