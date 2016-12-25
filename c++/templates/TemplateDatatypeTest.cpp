#include <cstring>
#include <memory>

#include "gtest/gtest.h"

namespace
{

template <class A_Type> class Reader
{
  public:
    std::shared_ptr<A_Type> ConvertToRgb(const char *s, const uint32_t sizeInBytes);
    uint32_t ConvertToSizeAType(const uint32_t sizeInBytes);
};

template <class A_Type> std::shared_ptr<A_Type> Reader<A_Type>::ConvertToRgb(const char *src, const uint32_t sizeInBytes)
{
    const uint32_t size = ConvertToSizeAType(sizeInBytes);
    std::shared_ptr<A_Type> ptr(new A_Type[size], std::default_delete<A_Type[]>());
    // C-style casting, because else warnings are generated 
    uint8_t* dst = (uint8_t*)(ptr.get());
    std::memcpy(dst, src, sizeInBytes);
    return ptr;
}

template <class A_Type> uint32_t Reader<A_Type>::ConvertToSizeAType(const uint32_t sizeInBytes)
{
    return sizeInBytes / sizeof(A_Type);
}

TEST(TemplateDatatypeTest, ConvertToSizeAType)
{
    Reader<uint8_t> reader8;
    ASSERT_EQ(2, reader8.ConvertToSizeAType(2));

    Reader<uint16_t> reader16;
    ASSERT_EQ(1, reader16.ConvertToSizeAType(2));
}

TEST(TemplateDatatypeTest, ReaderUint8)
{
    Reader<uint8_t> reader;

    std::shared_ptr<char> mem(new char[8], std::default_delete<char[]>());
    std::shared_ptr<uint8_t> rgb = reader.ConvertToRgb(mem.get(), 8);
}

TEST(TemplateDatatypeTest, ReaderUint16)
{
    Reader<uint16_t> reader;

    std::shared_ptr<char> mem(new char[8], std::default_delete<char[]>());
    std::shared_ptr<uint16_t> rgb = reader.ConvertToRgb(mem.get(), 8);
}

}  // namespace
