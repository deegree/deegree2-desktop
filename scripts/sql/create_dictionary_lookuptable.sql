-- This script is needed for the configuration of feature types / properties,
-- for which automatic dictionaries are to be generated. 
-- Tool in deegree: org.deegree.igeo.tools.DictionaryUpdater
-- 'id' must be unique. One could think of adding a sequence to do so. 'featuretype'
-- is to be added with the name of the feature type (without full qualification). 
-- Namespace must be added to 'namespace'. 'property' holds the name of the property,
-- for which the dictionary is to be created. dictionaryTable should be left empty, as 
-- it is filled automatically. Important is the 'updateMode', which specified, how to 
-- chope with changes of the dictionary.
-- 'n' drop and create new
-- 'l' lock
-- 'a' append
-- Name of table must be 'igeo_lookupdefinition'
CREATE TABLE igeo_lookupdefinition
(
  id number(10) not null,
  featuretype varchar2(500)  not null,
  property varchar2(500) not null,
  namespace varchar2(500) default 'http://www.deegree.org/app',
  dictionaryTable varchar2(50),
  updateMode char(1) default 'n'
)
;