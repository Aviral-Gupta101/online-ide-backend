"#include<iostream.h>\nusing namespace std;\n\nint main(){\n\n  cout<<\"hey guys c how u all doing\";\n  return 0;\n}"



"#include <chrono>\n#include <thread>\n\nint main() {\n    using namespace std::this_thread; // sleep_for, sleep_until\n    using namespace std::chrono; // nanoseconds, system_clock, seconds\n\n    sleep_for(nanoseconds(10));\n    sleep_until(system_clock::now() + seconds(10));\n}"