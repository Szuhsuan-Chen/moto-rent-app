import React from "react";

const Searchbar = () => {
  return (
    <div className="container py-5">
        <label for="branches">分店</label>
        <select id="branches" name="branches">
            <option value="apple">台北旗艦店</option>
            <option value="banana">台中概念店</option>
            <option value="orange">台南體驗店</option>
        </select>
      <input type="text" className="form-control" placeholder="搜尋車輛..." />
    </div>
  );
};

export default Searchbar;
