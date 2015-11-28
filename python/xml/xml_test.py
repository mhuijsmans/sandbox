# Using this ref as tutorial
# ref: https://docs.python.org/2/library/xml.etree.elementtree.html
# also contains xpath, xml-creation.

import xml.etree.ElementTree as ET

tree = ET.parse('country_data.xml')
root = tree.getroot()

for child in root:
	# top-level elements & attribute(s)
	print child.tag, child.attrib

for country in root.findall('country'):
	# read the value of the element <country><rank>..</rank></country>
	rank = country.find('rank').text
	name = country.get('name')
	print 'country, name: %s, rank: %s' % ( name, rank)

for neighbor in root.iter('neighbor'):
	# this prints all attributes
	print neighbor.attrib