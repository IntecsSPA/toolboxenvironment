declare namespace tst="http://test";
for $x in //tst:bookstore/tst:book where $x/tst:price>=30 return $x/tst:title
