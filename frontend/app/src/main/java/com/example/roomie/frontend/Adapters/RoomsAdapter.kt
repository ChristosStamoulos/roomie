package com.example.roomie.frontend.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomie.R
import com.example.roomie.backend.domain.Room
import java.util.Locale

class RoomsAdapter (private val roomList: ArrayList<Room>, private val onRoomClickListener: OnRoomClickListener) : RecyclerView.Adapter<RoomsAdapter.RoomViewHolder>() {
    interface OnRoomClickListener {
        fun onRoomClick(room: Room?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rooms_homescreen, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = roomList[position]
        holder.bindRoom(room)
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
            itemView.setOnClickListener {
                val adapterPosition = getAdapterPosition()
                if (adapterPosition != RecyclerView.NO_POSITION) {

                    if (adapterPosition < roomList.size) {
                        val clickedRoom = roomList[adapterPosition]
                        onRoomClickListener.onRoomClick(clickedRoom)
                    }
                }
            }
        }

        fun bindRoom(room: Room) {
            roomName = itemView.findViewById(R.id.name)
            roomImage = itemView.findViewById(R.id.roomImage)
            roomName.text = room.name
            roomArea.text = room.area
            roomStars.text = room.rating.toString()
            val resId = itemView.resources.getIdentifier(room.name.lowercase(Locale.getDefault()), "drawable", itemView.context.packageName)

            // Set the image based on the resource identifier
            if (resId != 0) {
                roomImage!!.setImageResource(resId)
            }
        }
    }
}