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
import com.example.roomie.backend.utils.SimpleCalendar

class ReservationsAdapter (private val roomList: ArrayList<Pair<Pair<Room, ArrayList<ByteArray>>,ArrayList<SimpleCalendar>>>, private val listener: onReservationClickListener) : RecyclerView.Adapter<ReservationsAdapter.ReservationsViewHolder>() {
        interface onReservationClickListener {
            fun onReservationClick(room: Room, view: View)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationsViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.reservation, parent, false)
            return ReservationsViewHolder(view)
        }

        override fun onBindViewHolder(holder: ReservationsViewHolder, position: Int) {
            val room = roomList[position]
            holder.bindRoom(room, listener)
        }

        override fun getItemCount(): Int {
            return roomList.size
        }

        inner class ReservationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var roomName: TextView
            var roomImage: ImageView? = null
            var roomArea: TextView
            var roomDate: TextView? = null

            init {
                roomName = itemView.findViewById(R.id.name)
                roomArea = itemView.findViewById(R.id.roomArea)
                roomDate = itemView.findViewById(R.id.date)
            }

            fun bindRoom(reservation: Pair<Pair<Room, ArrayList<ByteArray>>,ArrayList<SimpleCalendar>>, listener: onReservationClickListener) {
                val room = reservation.key
                val dates = reservation.value
                roomName = itemView.findViewById(R.id.name)
                roomImage = itemView.findViewById(R.id.roomImage)
                roomDate = itemView.findViewById(R.id.date)
                roomName.text = room.key.name
                roomArea.text = room.key.area

                roomDate!!.text = dates.get(0).toString() + " - " + dates.get(dates.size -1).toString()

                var bitmap: Bitmap? = BitmapFactory.decodeByteArray(room.value!![0], 0, room.value!![0].size)
                roomImage!!.setImageBitmap(bitmap)

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
                                            listener.onReservationClick(room.key, it)
                                        }
                                        .start()
                            }
                            .start()
                }
            }
        }
}