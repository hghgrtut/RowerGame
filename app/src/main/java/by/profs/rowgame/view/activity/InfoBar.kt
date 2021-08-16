package by.profs.rowgame.view.activity

interface InfoBar {
    fun showAll() {
        showDay()
        showFame()
        showMoney()
    }

    fun showDay()

    fun showFame()

    fun showMoney()

    fun nextAndShowDay()

    fun setFame(fame: Int)

    fun setMoney(money: Int)

    fun getDay(): Int

    fun getFame(): Int

    fun getMoney(): Int
}